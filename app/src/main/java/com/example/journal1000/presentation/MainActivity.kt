package com.example.journal1000.presentation

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.journal1000.R
import com.example.journal1000.databinding.ActivityMainBinding
import com.example.journal1000.domain.entity.GameType
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.entity.PlayerOrder
import com.example.journal1000.extensions.dpToIntPx
import com.example.journal1000.extensions.format
import com.example.journal1000.presentation.*
import com.example.journal1000.presentation.fragments.*
import com.example.journal1000.presentation.fragments.GameScoreListBaseFragment.Companion.GAME_SCORE_LIST
import java.util.*

class MainActivity : AppCompatActivity(), OnFragmentBehaviorControlManager {

    private lateinit var binding: ActivityMainBinding
    //private lateinit var viewModel: MainViewModel
    private lateinit var gameScoreList3PFragment: androidx.fragment.app.Fragment
    private lateinit var gameScoreList2PFragment: androidx.fragment.app.Fragment

    private val viewModel by lazy {
        ViewModelProvider(this)[GameViewModel::class.java]
    }

    private val vbSearchView
        get() = binding.searchView.binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val finish = intent.getBooleanExtra(FINISH, false) //default false if not set by argument
        if(finish) {
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupToolbar()

        setupSearchView()
        launchStartFragment()

        viewModel.isSearch.observe(this) {
            Log.d("Observe isSearch", "render ui it = $it")
            renderUi()
        }
        viewModel.gameSettingsStarted.observe(this) {
            launchNewGameSettingsFragment()
        }
        viewModel.isGameOn.observe(this){
            if (it) launchGameListFragment()
            //else
        }
        viewModel.searchState.observe(this) {
            launchGamesFragment(
                vbSearchView.etDateFrom.text.toString(),
                vbSearchView.etDateTo.text.toString()
            )
        }
        viewModel.auctionFinished.observe(this) {
            supportFragmentManager.popBackStack()
        }
        viewModel.showRules.observe(this) {
            launchRulesFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_load -> {
                supportFragmentManager.popBackStack()
                viewModel.handleSearchPanelOnShow()
            }
            R.id.action_new -> {
                Log.d("MainActivity", "NEW pressed")
                supportFragmentManager.popBackStack()
                viewModel.handleNew()
                //launchNewGameSettingsFragment()
            }
            R.id.action_save -> {
                viewModel.handleSave()
            }
            R.id.action_exit -> {
                viewModel.handleSave()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(FINISH, true)
                finish()
            }

            R.id.action_rules -> {
                viewModel.handleRules()
            }

        }
        return true
    }

    private fun renderUi() {
        supportFragmentManager.popBackStack(GAME_SCORE_LIST, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        Log.d("render ui", "isSearchShow = ${viewModel.isSearchShow}")
        if (viewModel.isSearchShow) {
            Log.d("render ui", "isSearchShow = ${viewModel.isSearchShow}")
            binding.searchView.open()
        } else binding.searchView.close()

        //if(viewModel.isGameOn) launchGameListFragment()
    }

    fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.overflowIcon = getDrawable(R.drawable.ic_baseline_menu_24)
        val logo = if (binding.toolbar.childCount > 2) binding.toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = dpToIntPx(40)
            it.height = dpToIntPx(40)
            it.marginEnd = dpToIntPx(16)
            logo.layoutParams = it
        }

        val title = binding.toolbar.getChildAt(0) as TextView
        val titleTypeFace: Typeface = Typeface.createFromAsset(assets, "fonts/zaragozac.ttf")
        title.typeface = titleTypeFace

        val subTitle = binding.toolbar.getChildAt(1) as TextView
        val subTitleTypeFace: Typeface = Typeface.createFromAsset(assets, "fonts/comediant-decor.ttf")
        subTitle.typeface = subTitleTypeFace
        subTitle.textSize = 12f
    }

    private fun setupSearchView() {
        vbSearchView.etDateFrom.setOnClickListener { showDateDialog(vbSearchView.etDateFrom) }
        vbSearchView.etDateTo.setOnClickListener { showDateDialog(vbSearchView.etDateTo) }
        vbSearchView.btnSearchOk.setOnClickListener {
            viewModel.handleSearchPanelOnShow()
            launchGamesFragment(
                vbSearchView.etDateFrom.text.toString(),
                vbSearchView.etDateTo.text.toString()
            )
            /*viewModel.handleSearchResult(
                vbSearchView.etDateFrom.toString(),
                vbSearchView.etDateTo.toString(),
        )*/ }


    }

    private fun showDateDialog(date_in: EditText) {
        val calendar: Calendar = Calendar.getInstance()
        val dateSetListener =
            OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date_in.setText(calendar.time.format())
            }
        DatePickerDialog(
            this@MainActivity,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun launchStartFragment() {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.playlist_container, StartFragment.newInstance())
            .commit()
    }

    private fun launchGamesFragment(startDate: String, endDate: String) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .addToBackStack(null)
            .replace(R.id.playlist_container, GamesFragment.newInstance(startDate, endDate))
            .commit()
    }

    private fun launchNewGameSettingsFragment() {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .addToBackStack(null)
            .replace(R.id.playlist_container, GameSettingsFragment.newInstance(
                viewModel.prefs.numberOfPlayers,
                viewModel.prefs.name1,
                viewModel.prefs.name2,
                viewModel.prefs.name3,
            ))
            .commit()
    }

    private fun launchGameListFragment() {
        Log.d(LOG_TAG, "launch List Fragment viewModel.players.size = ${viewModel.players.size}")
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        if (viewModel.gameType == GameType.THREE_PLAYER_GAME) {
            gameScoreList3PFragment = GameScoreListBaseFragment.newInstance(GameType.THREE_PLAYER_GAME)

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in_start,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                .replace(
                    R.id.playlist_container,
                    gameScoreList3PFragment
                )
                .addToBackStack(GAME_SCORE_LIST)
                .commit()
        } else {
            gameScoreList2PFragment = GameScoreListBaseFragment.newInstance(GameType.TWO_PLAYER_GAME)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in_start,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                .replace(
                    R.id.playlist_container,
                    gameScoreList2PFragment
                )
                .addToBackStack(GAME_SCORE_LIST)
                .commit()
        }
    }

    private fun launchRulesFragment() {
        Log.d(LOG_TAG, "launch rules Fragment")
        //supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .addToBackStack(null)
            .replace(R.id.playlist_container, RulesFragment.newInstance())
            .commit()
    }

    override fun onFinishSettingsEdition(playersNames: List<String>) {
        supportFragmentManager.popBackStack()
        viewModel.createNewGame(playersNames)
        launchGameListFragment()
    }

    override fun onFinishSavePointsEdition(points: List<Int>) {
        if (points.isNotEmpty()) {
            viewModel.handleCount(points)
        }
        supportFragmentManager.popBackStack()
    }

    override fun onFinishAuctionEdition(auctionData: Pair<PlayerOrder, Int>) {
        Log.d("MainActivity", "auctionData = ${auctionData.first} , ${auctionData.second}")
        viewModel.handleRequestPoints(auctionData)
    }

    override fun onGameSelected(gameWithScores: GameWithScores) {
        viewModel.handleSelectGame(gameWithScores)
    }

    override fun onStartSavePointsEdition(mode: Int) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .replace(R.id.playlist_container,
                PointsEditionFragment.newInstance(viewModel.gameType, viewModel.players.toTypedArray(), mode))
            .addToBackStack(null)
            .commit()
    }

    /*override fun onStartAuctionEdition(mode: Int) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .replace(R.id.playlist_container,
                PointsEditionFragment.newInstance(mode))
            .addToBackStack(null)
            .commit()
    }*/

    override fun onShowGameResult() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .replace(R.id.playlist_container,
                FinishGameFragment.newInstance(viewModel.gameType, viewModel.players.toTypedArray()))
            .addToBackStack(null)
            .commit()
    }

    /*private fun removeActiveFragment() {
        Log.d("MainActivity", "remove fragment")
        //val fragment = supportFragmentManager.findFragmentByTag(GAME_LIST)
        gameScoreListFragment?.let {
            Log.d("MainActivity", "removing fragment ${it.id}")
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
        Log.d("MainActivity", "fragment is = ${gameScoreListFragment.id}")
        viewModel.handleNew()
    }*/



    override fun onFinishGame() {
        supportFragmentManager.popBackStack(GAME_SCORE_LIST, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        viewModel.handleRestartPreparation()
        launchStartFragment()
    }

    override fun backToGameScoreList() {
        supportFragmentManager.popBackStack()
    }

    override fun removeRulesSheet() {
        supportFragmentManager.popBackStack()
    }

    companion object {
        const val LOG_TAG = "MainActivity"
        const val FINISH = "finish_key_extra"
    }
}