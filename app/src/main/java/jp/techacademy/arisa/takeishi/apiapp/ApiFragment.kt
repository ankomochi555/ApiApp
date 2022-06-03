package jp.techacademy.arisa.takeishi.apiapp


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


//API通信結果を表示するApiFragment.ktファイル
class ApiFragment: Fragment() {
    private val apiAdapter by lazy { ApiAdapter(requireContext()) } //apiAdapter プロパティは、前のセクションで作成した、RecyclerViewの表示のために必要となるAdapterクラス
    private val handler = Handler(Looper.getMainLooper())
    //handler プロパティは、別スレッドで動作させるために作成したもの。なぜ、必要なのかというと、描画はMainスレッドで行い、API通信の処理は別スレッドで行う必要があるから。
    //今回はAPI通信をした後に、そのデータをViewに反映させて描画させる必要がある。通信してデータを取得しても、そこは別スレッドなので、Viewにデータを渡して描画するのにMainスレッドに処理を跨がせる必要がある。

    private var fragmentCallback : FragmentCallback? = null // Fragment -> Activity にFavoriteの変更を通知する

    private var page = 0

    // Apiでデータを読み込み中ですフラグ。
    // 追加ページの読み込みの時にこれがないと、連続して読み込んでしまうので、それの制御のため
    //trueの時はAPI通信を発生させないBoolean変数
    private var isLoading = false //Falseなので、Loadingしていないの意味

    // 課題:クーポン詳細ページでもお気に入りの追加削除
    override fun onResume() { //updateViewを読んで画面をリロードする
        super.onResume()
        updateView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }
    }

    override fun onCreateView( //ActivityのsetContentView(R.layout.[layoutファイル])のように、Fragmentで使うレイアウトファイルを確定させるメソッド
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_api, container, false) //fragment_api.xmlが反映されたViewを作成して、returnします
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // ApiAdapterのお気に入り追加、削除用のメソッドの追加を行う
        apiAdapter.apply {
            onClickAddFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onAddFavorite(it)
            }
            onClickDeleteFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            // Itemをクリックしたとき
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }
        // RecyclerViewの初期化
        recyclerView.apply {
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext()) //一列ずつ表示

            addOnScrollListener(object: RecyclerView.OnScrollListener(){ // Scrollを検知するListenerを実装する。これによって、RecyclerViewの下端に近づいた時に次のページを読み込んで、下に付け足す
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {// dx はx軸方向の変化量(横) dy はy軸方向の変化量(縦) ここではRecyclerViewは縦方向なので、dyだけ考慮する
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy == 0) {// 縦方向の変化量(スクロール量)が0の時は動いていないので何も処理はしない
                        return
                    }
                    val totalCount = apiAdapter.itemCount // RecyclerViewの現在の表示アイテム数
                    val lastVisibleItem =  (layoutManager as LinearLayoutManager).findLastVisibleItemPosition() // RecyclerViewの現在見えている最後のViewHolderのposition
                    // totalCountとlastVisibleItemから全体のアイテム数のうちどこまでが見えているかがわかる(例:totalCountが20、lastVisibleItemが15の時は、現在のスクロール位置から下に5件見えていないアイテムがある)
                    // 一番下にスクロールした時に次の20件を表示する等の実装が可能になる。
                    // ユーザビリティを考えると、一番下にスクロールしてから追加した場合、一度スクロールが止まるので、ユーザーは気付きにくい
                    // ここでは、一番下から5番目を表示した時に追加読み込みする様に実装する
                    if (!isLoading && lastVisibleItem >= totalCount - 6) {// 読み込み中でない、かつ、現在のスクロール位置から下に5件見えていないアイテムがある
                        updateData(true)
                    }
                }
            })
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    fun updateView(){ // お気に入りが削除されたときの処理（Activityからコールされる）
        recyclerView.adapter?.notifyDataSetChanged() // RecyclerViewのAdapterに対して再描画のリクエストをする
    }

    private fun updateData(isAdd: Boolean = false) { //このメソッド内でAPI通信を行い、データを取得　ApiFragmentクラスで一番大切なメソッド
        if (isLoading) { //isLoadingの対応とisAddでpageの書き換え、startの計算が入っている
            return
        } else { //★ if文の中では何が起きている？　isLoading = true　ローディングしているの意味
            isLoading = true
        }
        if (isAdd) {
            page ++
        } else {
            page = 0
        }
        val start = page * COUNT + 1
        val url = StringBuilder()
                //append = 追加
            .append(getString(R.string.base_url)) // https://webservice.recruit.co.jp/hotpepper/gourmet/v1/
            .append("?key=").append(getString(R.string.api_key))// Apiを使うためのApiKey
            .append("&start=").append(start)// 何件目からのデータを取得するか
            .append("&count=").append(COUNT)// 1回で20件取得する
            .append("&keyword=").append(getString(R.string.api_keyword))// お店の検索ワード。ここでは例として「ランチ」を検索
            .append("&format=json")// ここで利用しているAPIは戻りの形をxmlかjsonが選択することができる。Androidで扱う場合はxmlよりもjsonの方が扱いやすいので、jsonを選択
            .toString()
        val client = OkHttpClient.Builder() //Http通信を行う本体
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object: Callback { //enqueue = 先に入れたものが先に出る構造になっている何か（キュー）にデータを入れること
            //↑この部分を入れることで、ログに通信の詳細を出すことができる
            //↓Callback内でHttp通信成功時と失敗時の処理を書く。
            override fun onFailure(call: Call, e: IOException) {// Error時の処理
                e.printStackTrace()
                handler.post {
                    updateRecyclerView(listOf(), isAdd) //空のリストを更新
                }
                isLoading = false // 読み込み中フラグを折る
            }

            override fun onResponse(call: Call, response: Response) {// 成功時の処理
                var list = listOf<Shop>()
                response.body?.string()?.also {
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java) //Jsonデータから ApiResponseへの変換を行う
                    list = apiResponse.results.shop //表示に使うShop型のListはApiResponseのプロパティresultsのshopで定義
                    //ApiResponseのインスタンスから、表示で必要なShop型のリストでRecyclerViewを更新し、表示
                }
                handler.post {
                    updateRecyclerView(list, isAdd) //取得したデータ（Json）をApiResponseというクラスに変換
                    //API通信により取得したShop型のListをRecyclerViewへ反映させるために、ApiAdapterのrefresh()というメソッドを呼び出している。
                }
                isLoading = false
            }
        })
    }

    private fun updateRecyclerView(list: List<Shop>, isAdd: Boolean){
        if (isAdd) { //修正したApiAdapterに対する、refreshやaddが実行される
            apiAdapter.add(list)
        } else {
            apiAdapter.refresh(list)
        }
        swipeRefreshLayout.isRefreshing = false // SwipeRefreshLayoutのくるくるを消す
    }

    companion object {
        private const val COUNT = 20 // 1回のAPIで取得する件数
    }
}