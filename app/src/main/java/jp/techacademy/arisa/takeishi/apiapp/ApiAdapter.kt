package jp.techacademy.arisa.takeishi.apiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

//AdapterクラスとViewHolderクラスの実装
//API通信によって取得されたJsonデータをKotlinのオブジェクトに変換させることができる
//その1件を読み込み、1行分のデータとして設定
//一覧画面では、お気に入りへの登録とお気に入りに登録されているものを削除、お気に入りの状態に応じ、表示させるアイコンを変化させる

class ApiAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 取得したJsonデータを解析し、Shop型オブジェクトとして生成したものを格納するリスト. 対象リストを保存するためのプロパティ
    private val items = mutableListOf<Shop>()

    // 一覧画面から登録するときのコールバック（FavoriteFragmentへ通知するメソッド) ★Unitの役目
    var onClickAddFavorite: ((Shop) -> Unit)? = null //Unit は戻り値のない関数

    // 一覧画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    // Itemを押したときのメソッド
    var onClickItem: ((Shop) -> Unit)? = null //〇6/2変更点


    // fun refresh(list: List<Shop>)は、このあと実装するApiFragmentから表示リスト更新時に呼び出すメソッド
    // itemsは表示させるShop型のリスト
    fun refresh(list: List<Shop>) {
        update(list, false)
    }

    fun add(list: List<Shop>) {
        update(list, true)
    }

    fun update(list: List<Shop>, isAdd: Boolean) {
        items.apply {
            if (!isAdd){ //追加の時は、Clearしない
                clear() //プロパティのitems を 空にする
            }
            addAll(list) //引数で受け取ったitemsにlistを全て追加する
        }
        notifyDataSetChanged() // recyclerViewを再描画させる
    }



    //以下の3つのメソッドは必ずオーバーライドしないといけないもの

    //このメソッドの戻り値の型である RecyclerView.ViewHolder型のオブジェクト生成が必要
    //RecyclerViewで表示させる1件分のデータを作成
    //RecyclerViewで表示させるViewHolderを作成。
    // 必要な数(getItemCount()の数ではなく、画面を覆うのに必要なViewHolderの数)が揃うまでは、
    // onCreateViewHolderが呼ばれるが、揃ったらもう作成されない
    //getItemCount()がたとえば100で、画面を覆うのに必要なViewHolderの数が15だった場合、
    // ViewHolderは多くても20くらいしか作られず、スクロールするたびに、画面外に出たViewHolderが使い回される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // ViewHolderを継承したApiItemViewHolderオブジェクトを生成し戻す inflate = 中に吹き込むイメージ
        return ApiItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
    }

    // ViewHolderを継承したApiItemViewHolderクラスの定義
    //このクラスは、RecyclerViewで表示させる1つ1つのセルのViewとなる。
    // つまり、必要な行数分だけonCreateViewHolder()メソッドで生成され、使いまわされる
    class ApiItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        // レイアウトファイルからidがrootViewのConstraintLayoutオブジェクトを取得し、代入
        val rootView : ConstraintLayout = view.findViewById(R.id.rootView)
        // レイアウトファイルからidがnameTextViewのCTextViewオブジェクトを取得し、代入
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)

        //レイアウトファイルから住所追加
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)

        // レイアウトファイルからidがimageViewのImageViewオブジェクトを取得し、代入
        val imageView: ImageView = view.findViewById(R.id.imageView)
        // レイアウトファイルからidがfavoriteImageViewのImageViewオブジェクトを取得し、代入
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }

    //表示させる（格納されている）件数を返すメソッド
    override fun getItemCount(): Int {
        // itemsプロパティに格納されている要素数を返す
        return items.size
    }

    //override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)メソッドでは、
    // 第１引数にonCreateViewHolderで作られたViewHolderが、
    // 第２引数に何番目の表示かが渡される。ここで、itemsからposition番のデータを取得して、
    // ViewHolderの描画する
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApiItemViewHolder){
            // 生成されたViewHolderがApiItemViewHolderだったら。。。
            updateApiItemViewHolder(holder, position)
        }// {
        // 別のViewHolderをバインドさせることが可能となる
        // }
    }

    //ApiItemViewHolderの更新を行っている
    private fun updateApiItemViewHolder(holder: ApiItemViewHolder, position: Int){
        // 生成されたViewHolderの位置を指定し、オブジェクトを代入
        val data = items[position]
        // お気に入り状態を取得
        val isFavorite = FavoriteShop.findBy(data.id) != null
        holder.apply {
            rootView.apply {
                // 偶数番目と奇数番目で背景色を変更させる
                setBackgroundColor(ContextCompat.getColor(context,
                    if(position % 2 == 0) android.R.color.white else android.R.color.darker_gray))
                setOnClickListener{
                    onClickItem?.invoke(data)
                    //↑Coupon.url（string文字列）ではなくてデータを渡す
                }
            }
            // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
            nameTextView.text = data.name
            addressTextView.text = data.address
            //Picassoライブラリを使い、imageViewにdata.logoImageのurlの画像を読み込ませる
            Picasso.get().load(data.logoImage).into(imageView)
            // 白抜きの星マークの画像を指定
            favoriteImageView.apply {
                // Picassoというライブラリを使ってImageVIewに画像をはめ込む
                setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
                setOnClickListener {
                    if (isFavorite) {
                        onClickDeleteFavorite?.invoke(data)
                    } else {
                        onClickAddFavorite?.invoke(data)
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }
}