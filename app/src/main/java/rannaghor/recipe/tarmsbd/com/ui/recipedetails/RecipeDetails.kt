package rannaghor.recipe.tarmsbd.com.ui.recipedetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import rannaghor.recipe.tarmsbd.com.R
import rannaghor.recipe.tarmsbd.com.adapter.ImageSliderAdapter
import rannaghor.recipe.tarmsbd.com.database.network.RannaghorRetrofitService
import rannaghor.recipe.tarmsbd.com.database.network.RetrofitClient
import rannaghor.recipe.tarmsbd.com.database.roompersistance.viewmodel.RannaghorViewModel
import rannaghor.recipe.tarmsbd.com.model.Recipe
import java.util.logging.Logger

class RecipeDetails : AppCompatActivity(R.layout.activity_recipe_details) {
    private val compositeDisposable = CompositeDisposable()

    private val retrofit = RetrofitClient.INSTANCE
    private val rannaghorRetrofitService = retrofit.create(RannaghorRetrofitService::class.java)

    private lateinit var rannaghorViewModel: RannaghorViewModel

    private lateinit var recipeName: TextView
    private lateinit var recipeDetail: TextView
    private lateinit var requiredMaterials: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeName = findViewById(R.id.recipe_name)
        recipeDetail = findViewById(R.id.textviewRecipe)
        requiredMaterials = findViewById(R.id.textviewMaterials)

        try {
            val recipe = intent.getParcelableExtra<Recipe>(RECIPE_DETAIL)

            supportActionBar?.title = recipe?.name?.trim()
            recipeName.text = recipe?.name?.trim()
            recipeDetail.text = recipe?.recipe?.trim()
            requiredMaterials.text = recipe?.materials?.trim()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        val sliderView = findViewById<SliderView>(R.id.imageSlider)
        val adapter = ImageSliderAdapter(applicationContext)

        val images = mutableListOf(
            R.mipmap.ic_launcher,
            R.drawable.ic_juice,
            R.drawable.ic_nasta,
            R.drawable.ic_burger_2,
            R.drawable.ic_burger
        )

        adapter.renewItems(images)

        sliderView.apply {
            setSliderAdapter(adapter)
            setIndicatorAnimation(IndicatorAnimations.WORM)
        }
        rannaghorViewModel = ViewModelProvider(this).get(RannaghorViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_detail_appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()

            R.id.menu_like_recipe -> {
                val recipe = intent.getParcelableExtra<Recipe>(RECIPE_DETAIL)
                recipe?.let {
                    if (it.liked == 0) {
                        it.liked = 1
                        Toast.makeText(
                            applicationContext,
                            "Recipe Saved to Saved List!",
                            Toast.LENGTH_SHORT
                        ).show()

                        compositeDisposable.add(
                            rannaghorRetrofitService.recipe
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { recipes ->
                                        Logger.getLogger("Recipe Detail").warning("LIKED")
                                        rannaghorViewModel.insertRecipes(recipes)
                                    }, this::handleError
                                )
                        )
                    } else {
                        it.liked = 0
                        Toast.makeText(
                            applicationContext,
                            "Recipe Removed From Saved List!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    rannaghorViewModel.updateRecipe(it)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleError(error: Throwable) {
        Logger.getLogger("Recipe Detail")
            .warning("   Error: ${error.localizedMessage}")
    }

    companion object {
        const val RECIPE_DETAIL = "rannaghor.recipe.tarmsbd.com.ui.main.recipe_detail"
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}