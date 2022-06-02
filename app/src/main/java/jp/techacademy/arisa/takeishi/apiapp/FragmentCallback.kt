package jp.techacademy.arisa.takeishi.apiapp

interface FragmentCallback {
    // Itemを押したときの処理
    //fun onClickItem(url: String)

    // onClickItem(url: String)を削除した理由　FavoriteShopのほうが使いまわせるから??
    //fun onClickItem(Shop: Shop)の役割
    // FavoriteShopをわたす　funの追加　onclickitemShopをわたすfunの追加ApiResponce

    fun onClickItem(FavoriteShop: String)

    fun onClickItem(Shop: Shop)

    // お気に入り追加時の処理
    fun onAddFavorite(shop: Shop)

    // お気に入り削除時の処理
    fun onDeleteFavorite(id: String)
}