package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.R
import kotlinx.android.synthetic.main.fragment_place.*

//对Place相关Fragment进行实现，所以要继承Fragment()类
class PlaceFragment : Fragment() {

    //使用懒加载技术创建ViewModel对象
    private val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    //延迟加载适配器
    private lateinit var adapter: PlaceAdapter

    //当创建该View时加载布局为fragment_place
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    //加载将UI需要的数据，以及创建对数据的跟踪逻辑
    //虽然onActivityCreated()方法是过时的方法，但是现在并没有很好的解决方案
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //设置RecyclerView的layoutManager与adapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(viewModel.placeList)
        recyclerView.adapter = adapter

        //监听搜索框内容的变化情况
        //当搜索框内容为空时将背景图重新加载回来，RecyclerView设置为不可见，数据设置为空
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        //借助LiveData获取服务器响应的数据，当有任何数据变化时，或调到Observer接口实现中
        viewModel.placeLiveData.observe(viewLifecycleOwner) { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

}