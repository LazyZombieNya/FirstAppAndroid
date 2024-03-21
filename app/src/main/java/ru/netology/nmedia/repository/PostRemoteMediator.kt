package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)

class PostRemoteMediator(
    private val service: ApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id!=null){
                       service.getLatest(state.config.initialLoadSize)
                    }else{
                    service.getLatest(state.config.pageSize)
                    }
                }
            }
            if (!result.isSuccessful) {
                throw ApiError(result.code(), result.message())
            }
            val body = result.body() ?: throw ApiError(
                result.code(),
                result.message(),
            )
            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        //postDao.clear()
                        if (postDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.KeyType.AFTER,
                                        body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.KeyType.BEFORE,
                                        body.last().id,
                                    ),
                                )
                            )
                        } else{
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id,
                                )
                            )
                        }
                    }


                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id,
                            ),

                            )
                    }
                    else ->Unit
                }


                postDao.insert(body.map(PostEntity::fromDto))
            }
                        return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }


}