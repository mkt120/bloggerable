package com.mkt120.bloggerable.top.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mkt120.bloggerable.BloggerableApplication
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.RealmManager
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.TopActivity
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
        public const val LIST_POSTS = 1
        public const val LIST_DRAFT = 2
        fun newInstance(blogId: String, listType: Int): PostsListFragment =
            PostsListFragment().apply {
                val bundle = Bundle().apply {
                    putString(EXTRA_BLOGS_ID, blogId)
                    putInt(EXTRA_LIST_TYPE, listType)
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
        val type = arguments!!.getInt(EXTRA_LIST_TYPE)

        // FIXME:
        if (requireActivity().application is BloggerableApplication) {
            val realm = (requireActivity().application as BloggerableApplication).getRealm()
            postsListPresenter =
                PostsListPresenter(RealmManager(realm), this@PostsListFragment, blogId!!, type)
            postsListPresenter.onActivityCreated()
        }
    }

    override fun setPostsResponse(response: List<Posts>) {
        // todo:ちょっと微妙
        recycler_view.adapter =
            PostsAdapter(
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

    override fun showPostsItem(posts: Posts, type: Int) {
        // todo:ちょっと微妙
        if (activity is TopActivity) {
            (activity as TopActivity).onClickPostsItem(
                posts,
                type
            )
        }
    }

}
