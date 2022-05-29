package jp.techacademy.arisa.takeishi.apiapp

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FavoriteShop: RealmObject() {
    @PrimaryKey
    var id: String = "" //プライマリーキーはidのみにかかっている　id = データに割り振る番号　学籍番号みたいな感じ
    var imageUrl: String = ""
    var name: String = ""
    var address: String = "" //住所追加
    var url: String = ""

//アプリが落ちてしまう


    companion object{
        fun findAll(): List<FavoriteShop> = // お気に入りのShopを全件取得
            Realm.getDefaultInstance().use { realm ->
                realm.where(FavoriteShop::class.java) //検索するモデルを書く　whereの中はどのオブジェクトの中を検索するか
                    .findAll().let {
                        realm.copyFromRealm(it)
                    }
            }

        fun findBy(id: String): FavoriteShop? = // お気に入りされているShopをidで検索して返す。お気に入りに登録されていなければnullで返す
            Realm.getDefaultInstance().use { realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()?.let {
                        realm.copyFromRealm(it)
                    }
            }

        fun insert(favoriteShop: FavoriteShop) = // お気に入り追加
            Realm.getDefaultInstance().executeTransaction{
                it.insertOrUpdate(favoriteShop)
            }

        fun delete(id: String) = // idでお気に入りから削除する
            Realm.getDefaultInstance().use { realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()?.also { deleteShop ->
                        realm.executeTransaction{
                            deleteShop.deleteFromRealm()
                        }
                    }
            }
    }
}