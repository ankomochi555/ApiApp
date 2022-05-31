package jp.techacademy.arisa.takeishi.apiapp

interface FragmentCallback {
    // Itemを押したときの処理
    fun onClickItem(url: String) //FavoriteShopをわたす　funの追加　onclickitemShopをわたすfunの追加ApiResponce

   // fun onClickItem(FavoriteShop: String)

   // fun onClickItem(Shop: String)

    // お気に入り追加時の処理
    fun onAddFavorite(shop: Shop)

    // お気に入り削除時の処理
    fun onDeleteFavorite(id: String)
}