package rannaghor.recipe.tarmsbd.com.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rannaghor.recipe.tarmsbd.com.R
import rannaghor.recipe.tarmsbd.com.model.Recipe
import rannaghor.recipe.tarmsbd.com.service.OnClickEventListener

class AllRecipeAdapter(private val context: Context, private val recipes: List<Recipe>) :
    RecyclerView.Adapter<AllRecipeAdapter.RecipeHolder>() {

    private lateinit var onClickEventListener: OnClickEventListener

    class RecipeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.comment_username)
        private val likes = itemView.findViewById<TextView>(R.id.recipe_likes_count)
        private val icon = itemView.findViewById<ImageView>(R.id.recipe_image)
        @SuppressLint("DefaultLocale", "SetTextI18n")
        fun bind(context: Context, recipe: Recipe) {
            name.text = recipe.name?.trim()
            likes.text = "${recipe.likes}"
            if (recipe.type?.trim() == "panio")
                Glide.with(context).load(R.drawable.ic_juice).into(icon)
            else Glide.with(context).load(R.drawable.ic_nasta).into(icon)

            icon.clipToOutline = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val holder = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_item, parent, false)
        return RecipeHolder(holder)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        holder.bind(context, recipe = recipes[position])
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onClickEventListener.onItemClickListener(holder.adapterPosition)
            }
        }
    }

    public fun setOnClickEventListener(onClickEventListener: OnClickEventListener) {
        this.onClickEventListener = onClickEventListener
    }
}