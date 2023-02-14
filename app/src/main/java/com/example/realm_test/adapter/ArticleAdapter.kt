package com.example.realm_test.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.realm_test.databinding.ArticleItemsBinding
import com.example.realm_test.model.ArticleModel

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    var onUpdateClick: ((ArticleModel) -> Unit)? = null
    var onDeleteClick: ((ArticleModel) -> Unit)? = null

    class ArticleViewHolder(val articleBinding: ArticleItemsBinding) :
        RecyclerView.ViewHolder(articleBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemsBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val articles = differ.currentList[position]
        holder.articleBinding.apply {
            tvTitle.text = articles.title.toString()
            tvDesc.text = articles.description.toString()

            updateBtn.setOnClickListener {
                onUpdateClick?.invoke(articles)
            }
            deleteBtn.setOnClickListener {
                onDeleteClick?.invoke(articles)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {

        override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
            return newItem.id == oldItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)
}