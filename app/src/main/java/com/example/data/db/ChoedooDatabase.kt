package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.MenuItemEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        MenuItemEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChoedooDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: ChoedooDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ChoedooDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChoedooDatabase::class.java,
                    "choedoo_coffee_pos.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: ChoedooDatabase) {
            val userDao = database.userDao()
            val menuDao = database.menuItemDao()

            if (userDao.getUserCount() == 0) {
                userDao.insertUsers(
                    listOf(
                        UserEntity(
                            username = "admin",
                            password = "admin",
                            role = "ADMIN",
                            name = "Admin CHOEDOO"
                        ),
                        UserEntity(
                            username = "rider1",
                            password = "123",
                            role = "RIDER",
                            name = "Rider 1 - Dimas"
                        ),
                        UserEntity(
                            username = "rider2",
                            password = "123",
                            role = "RIDER",
                            name = "Rider 2 - Kevin"
                        ),
                        UserEntity(
                            username = "rider3",
                            password = "123",
                            role = "RIDER",
                            name = "Rider 3 - Rizky"
                        )
                    )
                )
            }

            if (menuDao.getMenuItemCount() == 0) {
                menuDao.insertMenuItems(
                    listOf(
                        MenuItemEntity(
                            id = 1,
                            name = "Kopi Susu Basic",
                            price = 10000L,
                            stock = 30,
                            category = "Coffee"
                        ),
                        MenuItemEntity(
                            id = 2,
                            name = "Kopi Susu Power Strong",
                            price = 12000L,
                            stock = 25,
                            category = "Coffee"
                        ),
                        MenuItemEntity(
                            id = 3,
                            name = "Kopi Susu Butterscotch",
                            price = 15000L,
                            stock = 20,
                            category = "Coffee"
                        ),
                        MenuItemEntity(
                            id = 4,
                            name = "Kopi Susu Salted Caramel",
                            price = 15000L,
                            stock = 20,
                            category = "Coffee"
                        ),
                        MenuItemEntity(
                            id = 5,
                            name = "Thai Tea",
                            price = 8000L,
                            stock = 30,
                            category = "Tea"
                        ),
                        MenuItemEntity(
                            id = 6,
                            name = "Thai Tea Caramel",
                            price = 10000L,
                            stock = 25,
                            category = "Tea"
                        )
                    )
                )
            }
        }
    }
}
