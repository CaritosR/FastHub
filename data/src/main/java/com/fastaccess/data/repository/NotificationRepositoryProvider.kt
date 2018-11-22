package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import bz.oron.rxlivedata.map
import com.fastaccess.data.model.GroupedNotificationsModel
import com.fastaccess.data.persistence.dao.NotificationsDao
import com.fastaccess.data.persistence.models.NotificationModel
import javax.inject.Inject

/**
 * Created by Kosh on 22.06.18.
 */
class NotificationRepositoryProvider @Inject constructor(private val dao: NotificationsDao) : NotificationRepository {
    override fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel> = dao.getNotifications(unread)
    override fun getAllNotifications(): LiveData<List<GroupedNotificationsModel>> = dao.getAllNotifications(false)
        .map(groupNotifications())

    override fun getMainNotifications(): LiveData<List<NotificationModel>> = dao.getMainNotifications()
    override fun insert(model: NotificationModel): Long = dao.insert(model)
    override fun insert(model: List<NotificationModel>) = dao.insert(model)
    override fun update(model: NotificationModel): Int = dao.update(model)
    override fun delete(model: NotificationModel) = dao.delete(model)
    override fun deleteAll(unread: Boolean) = dao.deleteAll(unread)

    /**
     * Fixes Cannot infer a type for this parameter. Please specify it explicitly. 🤷‍🤷‍🤷‍🤷‍🤷‍🤷‍
     */
    private fun groupNotifications(): (List<NotificationModel>) -> List<GroupedNotificationsModel> {
        return { list: List<NotificationModel> ->
            list.groupBy { it.repository }
                .flatMap { entry ->
                    val notifications = arrayListOf<GroupedNotificationsModel>()
                    notifications.add(GroupedNotificationsModel(GroupedNotificationsModel.HEADER, entry.key))
                    notifications.addAll(entry.value.map { GroupedNotificationsModel(GroupedNotificationsModel.CONTENT, notification = it) })
                    return@flatMap notifications
                }
        }
    }
}

interface NotificationRepository {
    fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel>
    fun getAllNotifications(): LiveData<List<GroupedNotificationsModel>>
    fun getMainNotifications(): LiveData<List<NotificationModel>>
    fun insert(model: NotificationModel): Long
    fun insert(model: List<NotificationModel>)
    fun update(model: NotificationModel): Int
    fun delete(model: NotificationModel)
    fun deleteAll(unread: Boolean)
}