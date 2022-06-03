package jp.techacademy.arisa.takeishi.apiapp

interface FragmentCallback {
    // Itemを押したときの処理
    //fun onClickItem(url: String)

    // onClickItem(url: String)を削除した理由　どっちからもお気に入り登録できるようにするため、URLだけでなくお気に入り登録/削除の情報を渡す必要があるから
    //fun onClickItem(Shop: Shop)の役割
    // FavoriteShopをわたす　funの追加　onclickitemShopをわたすfunの追加ApiResponce

    fun onClickItem(favoriteShop: FavoriteShop) //FavoriteAdapter用

    fun onClickItem(shop: Shop) //ApiAdapter用

    // お気に入り追加時の処理
    fun onAddFavorite(shop: Shop)

    // お気に入り削除時の処理
    fun onDeleteFavorite(id: String)
}