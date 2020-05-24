package com.mkt120.bloggerable.top.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mkt120.bloggerable.BloggerableApplication
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.PostsRepository
import com.mkt120.bloggerable.top.TopActivity
import com.mkt120.bloggerable.top.TopContract
import com.mkt120.bloggerable.usecase.FindAllPosts
import kotlinx.android.synthetic.main.fragment_posts_list.*

/**
 * 記事一覧を表示するFragment
 */
class PostsListFragment : Fragment(),
    PostsListContract.PostsListView {
    companion object {
        private val TAG = PostsListFragment::class.java.simpleName
        private const val EXTRA_BLOGS_ID = "EXTRA_BLOGS_ID"
        private const val EXTRA_LIST_TYPE = "EXTRA_LIST_TYPE"
        fun newInstance(blogId: String?, type: TopContract.TYPE): PostsListFragment =
            PostsListFragment().apply {
                val bundle = Bundle().apply {
                    putString(EXTRA_BLOGS_ID, blogId)
                    putSerializable(EXTRA_LIST_TYPE, type)
                }
                arguments = bundle
            }
    }

    private lateinit var postsListPresenter: PostsListContract.PostsListPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_posts_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val blogId = arguments!!.getString(EXTRA_BLOGS_ID)
        val type: TopContract.TYPE =
            arguments!!.getSerializable(EXTRA_LIST_TYPE) as TopContract.TYPE

        // FIXME:
        if (requireActivity().application is BloggerableApplication) {
            val realm = (requireActivity().application as BloggerableApplication).getRealm()
            val postsRepository =
                PostsRepository(BloggerApiDataSource(), RealmDataSource(realm))
            postsListPresenter =
                PostsListPresenter(
                    FindAllPosts(postsRepository),
                    this@PostsListFragment,
                    blogId,
                    type
                )
            postsListPresenter.onActivityCreated()
        }
    }

    override fun setPostsResponse(type: TopContract.TYPE, response: List<Posts>) {
        // todo:ちょっと微妙
        recycler_view.adapter =
            PostsAdapter(
                type,
                response,
                object :
                    PostsAdapter.PostsClickListener {
                    override fun showItem(posts: Posts) {
                        if (activity?.isFinishing == null || activity!!.isFinishing) {
                            return
                        }
                        postsListPresenter.onClickPosts(posts)
                    }
                })
    }

    override fun showPostsItem(type: TopContract.TYPE, posts: Posts) {
        // todo:ちょっと微妙
        if (activity is TopActivity) {
            (activity as TopActivity).onClickPostsItem(
                posts,
                type
            )
        }
    }

}
