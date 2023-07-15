package com.example.journal1000.presentation

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.journal1000.App
import com.example.journal1000.R
import com.example.journal1000.data.DataHolder
import com.example.journal1000.data.GamePreferences
import com.example.journal1000.data.MessageHolder
import com.example.journal1000.data.PrefManager
import com.example.journal1000.data.db.AppDatabase
import com.example.journal1000.data.UserScheme.GAME_ID
import com.example.journal1000.data.UserScheme.NAME_ONE
import com.example.journal1000.data.UserScheme.NAME_THREE
import com.example.journal1000.data.UserScheme.NAME_TWO
import com.example.journal1000.data.UserScheme.NUM_OF_PLAYERS
import com.example.journal1000.data.markdown.MarkdownParser
import com.example.journal1000.domain.entity.*
import com.example.journal1000.domain.entity.GameType.THREE_PLAYER_GAME
import com.example.journal1000.domain.entity.GameType.TWO_PLAYER_GAME
import com.example.journal1000.domain.entity.PlayerOrder.*
import com.example.journal1000.domain.usecases.*
import com.example.journal1000.presentation.adapters.GameListItem
import com.example.journal1000.presentation.markdown.MarkdownBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException
import java.util.*
import kotlin.math.round

class GameViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private val repository = AppDatabase.getInstance(application).gameDao()
    private val gameFactory: GameFactory = GameFactory(this)
    private val prefManager: PrefManager = PrefManager(getApplication())
    lateinit var prefs: GamePreferences

    private val _showRules = MutableLiveData<Unit>()
    val showRules: LiveData<Unit>
        get() = _showRules

    private val _isSearch = MutableLiveData(false)
    val isSearch: LiveData<Boolean>
        get() = _isSearch
    var isSearchShow = false

    private val _isGameOn = MutableLiveData(false)
    val isGameOn: LiveData<Boolean>
        get() = _isGameOn

    private val _gameSettingsStarted = MutableLiveData<Unit>()
    val gameSettingsStarted: MutableLiveData<Unit>
        get() = _gameSettingsStarted

    private var _gameList: MutableLiveData<MutableList<GameListItem>> = MutableLiveData()
    val gameList: LiveData<MutableList<GameListItem>>
        get() = _gameList

    var currentGameList: MutableList<GameListItem> = mutableListOf()
        get() = _gameList.value?.toMutableList() ?: mutableListOf()

    private val _searchState = MutableLiveData<Unit>()
    val searchState: LiveData<Unit>
        get() = _searchState

    private var _game: Game? = null
    val game: Game
        get() = _game ?: throw RuntimeException("Game is null")

    val gameType: GameType
        get() = game.type

    private var _scores: MutableList<Score> = mutableListOf()
    val scores: List<Score>
        get() = _scores.toList()

    private lateinit var currentScore: Score

    private var _players: MutableList<Player> = mutableListOf()
    val players: List<Player>
        get() = _players.toList()

    private var prevPlayers: List<Player> = listOf()

    private var _isGameFinished = MutableLiveData(false)
    val isGameFinished
        get() = _isGameFinished

    private var _isBackStepPressed = MutableLiveData(true)
    val isBackStepPressed: LiveData<Boolean>
        get() = _isBackStepPressed

    private var currentGameId = -1L

    private var onBarrel: PlayerOrder? = null

    private var _messagesGameList = MutableLiveData<MutableList<String>>()
    val messagesGameList: LiveData<MutableList<String>>
        get() = _messagesGameList

    private var _auctionFinished = MutableLiveData<Unit>()
    val auctionFinished: LiveData<Unit>
        get() = _auctionFinished

    private val getGameWithScoresUseCase = GetGameWithScoresUseCase(repository)
    private val saveGameWithScoresUseCase = SaveGameWithScoresUseCase(repository)

    init {
        Log.d("Init Block", "Block started")
        loadPreferences()
    }

    fun startGame() {
        _isGameOn.value = true
    }

    fun loadGame(id: Long) {
        viewModelScope.launch {
            val gameWithScores: GameWithScores? = getGameWithScoresUseCase.getGameWithScores(id)
            if (gameWithScores != null) {
                restoreGameVariables(gameWithScores)
                restoreGameList()
                startGame()
            } else {
                Toast.makeText(getApplication(), R.string.game_not_found, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveGame() {
        var id: Long = -1
        runBlocking {
            val job: Job = launch {
                id = saveGameWithScoresUseCase.saveGameWithScores(game, scores, players)
            }
            job.join()
            if (id != -1L && id != currentGameId) {
                _game?.let { game ->
                    game.gameId = id
                    _players.map { it.gameId = id }
                    _scores.map { it.gameId = id }
                }
                currentGameId = id
                savePreferences()
            }
        }

        players.forEach { Log.d("VM Save Game", "player ${it.name} game_id = ${it.gameId}") }
        scores.forEach { Log.d("VM Save Game", "score ${it.step} game_id = ${it.gameId}") }
    }

    fun createNewGame(playersNames: List<String>) {
        _game = gameFactory.getNewGameInstance(playersNames.size)
        for (i in playersNames.indices) {
            PlayerFactory.getNewPlayerInstance(playersNames[i], i)
                .also {
                    _players.add(it)
                }
        }
        _players[PLAYER_ONE].onHundred = true

        saveGame()
        startGame()
    }

    fun handleRules() {
        _showRules.value = Unit
    }

    fun handleNew() {
        clearGameData()
        loadPreferences()
        startNewGame()
    }

    fun handleRestartPreparation() {
        savePreferences()
        clearGameData()
    }

    fun handleSave() {
        _game?.let {
            saveGame()
        }
    }

    fun handleSearchPanelOnShow() {
        isSearchShow = !isSearch.value!!
        _isSearch.value = isSearchShow
    }

    fun handleSelectGame(gameWithScores: GameWithScores) {
        restoreGameVariables(gameWithScores)
        restoreGameList()
        startGame()
        savePreferences()
    }

    fun handleCount(points: List<Int>) {

        MessageHolder.clear(MessageHolder.GAME_SCORE_LIST_DEST)
        prevPlayers = players.map { player -> player.copy() }.toList()
        _isBackStepPressed.value = false

        val roundedPoints = points.map { (round(it.toDouble() / 5) * 5).toInt() }
        val consistentPoints = Array(points.size){0}

        changePlayerOnHundredNumber()

        for (i in points.indices) {
            consistentPoints[i] = drivePlayerPointsToBeConsistent(_players[i], roundedPoints[i])
            _players[i].count += consistentPoints[i]
            _players[i].countInTime = consistentPoints[i]

            checkPlayerForBolt(players[i], roundedPoints[i]) // consistent points может быть 0 , но фактически игрок наюр
            checkPlayerForBarrel(players[i])
            checkPlayerForResetCount(players[i])
            checkForPlayerPointOverflow(players[i])

            _players[i].onHundred = i == game.onHundredPlayer.value
        }
        reCheckBarrelDataOfPlayers()
        val playersCounts = players.map { it.count }.toMutableList()

        currentScore = gameFactory.getScoreInstance(playersCounts).also { _scores.add(it) }

        val newGameListItem = gameFactory.createGameListItem(consistentPoints.toList(), playersCounts)
        val newGameList = currentGameList.toMutableList()
        newGameList.add(newGameListItem)
        _gameList.value = newGameList

        checkForGameFinished()
        setDefaultRequestPoints()
        saveGame()
    }

    fun handleCancelCount() {
        _scores.removeLast()
        _players = prevPlayers.toMutableList()
        _game = game.copy(
            isGameFinished = false,
            winner = null,
        )
        _game?.hasWinner = false
        changePlayerOnHundredNumberReverse()

        val list = currentGameList.toMutableList()
        list.removeLast()
        _gameList.value = list

        _isGameFinished.value = false
        _isBackStepPressed.value = true
    }

    @SuppressWarnings("SuspiciousIndentation")
    fun handleRequestPoints(auctionData: Pair<PlayerOrder, Int>) {
        clearAuctionData()
        _players.map {
            if (it.playerOrder == auctionData.first) {
            it.requestedPoints = auctionData.second
            _auctionFinished.value = Unit
            } else {
                it.requestedPoints = 0
            }
        }
    }

    private fun setDefaultRequestPoints() {
        clearAuctionData()
        _players.map { if (it.onHundred) it.requestedPoints = 100 }
    }

    fun handleLastGameLoading() {
        loadGame(prefs.id)
    }

    fun clearAuctionData() {
        _players.map { it.requestedPoints = 0 }
    }

    private fun finishGame() {
        _game?.isGameFinished = true
        _isGameFinished.value = true
        _isGameOn.value = false
    }

    private fun startNewGame() {
        _gameSettingsStarted.value = Unit
    }

    private fun drivePlayerPointsToBeConsistent(player: Player, points: Int): Int {
        return if (player.isOnBarrel && player.onHundred) {
            if (player.requestedPoints > WIN_THRESHOLD_ON_BARREL) {
                when (points) {
                    in player.requestedPoints..400 -> player.requestedPoints
                    else -> (-1) * player.requestedPoints
                }
            } else if (player.requestedPoints > 0) {
                FINE_FOR_BARREL_OFF
            } else points
        } else if (player.requestedPoints >= 100) {
            when (points) {
                in player.requestedPoints..400 -> player.requestedPoints
                else -> player.requestedPoints * (-1)
            }
        } else points
    }

    private fun checkPlayerForBolt(player: Player, points: Int) {
        if (points == 0) {
            player.boltNumber++
            createMessage(R.string.player_get_bolt, player.name)
        }
        if (player.boltNumber == 3) {
            player.count -= 120
            player.boltNumber = 0
            createMessage(R.string.player_pay_fine_bolt, player.name)
        }
    }

    private fun checkPlayerForBarrel(player: Player) { // могут ли два игрока быть набочке??
        if (player.count >= ON_BARREL_VALUE) { //
            if (player.isOnBarrel) {  //если уже был на бочке?
                player.onBarrelAttemptCount++
                if (player.count >= WIN_VALUE) { // если сейчас набрал нужные очки сидя на бочке
                    player.isWinner = true
                    player.onBarrelAttemptCount = 0
                    player.isOnBarrel = false
                } else if (player.onBarrelAttemptCount == 3 || onBarrel != player.playerOrder) { // Если  сейчас не набрал нужное кол-во +1 к бочке и 3 бочкм - штраф если другой игрок уже влез на бочку
                    getOffFromBarrel(player)
                } else player.count = ON_BARREL_VALUE
            } else { // если не на бочке но набрал >= 880
                player.isOnBarrel = true
                player.count = ON_BARREL_VALUE
                onBarrel = player.playerOrder
                createMessage(R.string.player_on_barrel, player.name)
            }
        } else if (player.isOnBarrel) getOffFromBarrel(player)
    }

    private fun reCheckBarrelDataOfPlayers() {
        players.forEach{ player ->
            if (player.playerOrder != onBarrel && player.count >= ON_BARREL_VALUE && !player.isWinner) {
                getOffFromBarrel(player)
            }
        }
        if (!players.any { it.isOnBarrel }) onBarrel = null
    }

    private fun getOffFromBarrel(player: Player) {
        if (player.count == ON_BARREL_VALUE || player.count > OFF_BARREL_VALUE) {
            player.count = OFF_BARREL_VALUE
        }
        player.onBarrelAttemptCount = 0
        player.isOnBarrel = false
        createMessage(R.string.player_off_barrel, player.name)
    }

    private fun checkPlayerForResetCount(player: Player) {
        if (player.count == RESET_TO_ZERO_VALUE) {
            player.count = 0
            createMessage(R.string.player_reset, player.name)
        }
    }

    private fun checkForPlayerPointOverflow(player: Player) {
        if (player.count >= WIN_VALUE && !game.hasWinner) {
            _game?.winner = player.name
            _game?.hasWinner = true
            createMessage(R.string.player_win, player.name)
        }
    }

    private fun checkForGameFinished() {
        if (game.hasWinner) finishGame()
    }

    private fun clearGameData() {
        _players.clear()
        _scores.clear()
        _game = null
        _gameList.value = mutableListOf()
        _isGameFinished.value = false
    }

    private fun changePlayerOnHundredNumber() {
        if (gameType == THREE_PLAYER_GAME) {
            when (game.onHundredPlayer) {
                ONE -> _game?.onHundredPlayer = TWO
                TWO -> _game?.onHundredPlayer = THREE
                THREE -> _game?.onHundredPlayer = ONE
            }
        } else {
            when (game.onHundredPlayer) {
                ONE -> game?.onHundredPlayer = TWO
                TWO -> game?.onHundredPlayer = ONE
                else -> {
                    throw RuntimeException("Incorrect number of players")
                }
            }
        }
    }

    private fun changePlayerOnHundredNumberReverse() {
        if (gameType == THREE_PLAYER_GAME) {
            when (game.onHundredPlayer) {
                ONE -> _game?.onHundredPlayer = THREE
                TWO -> _game?.onHundredPlayer = ONE
                THREE -> _game?.onHundredPlayer = TWO
            }
        } else {
            when (game.onHundredPlayer) {
                ONE -> _game?.onHundredPlayer = TWO
                TWO -> _game?.onHundredPlayer = ONE
                else -> {
                    throw RuntimeException("Incorrect number of players")
                }
            }
        }
    }

    private fun savePreferences() {
        Log.d("Save prefs", "id = $currentGameId")
        viewModelScope.launch {
            if (gameType.value == 3) {
                prefManager.writeData(
                    currentGameId,
                    game.type.value,
                    players[0].name,
                    players[1].name,
                    players[2].name
                )
            } else {
                prefManager.writeData(
                    currentGameId,
                    game.type.value,
                    players[0].name,
                    players[1].name
                )
            }
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            prefs = prefManager.readData(GAME_ID, NUM_OF_PLAYERS, NAME_ONE, NAME_TWO, NAME_THREE)
        }
    }

    private fun restoreGameVariables(gameWithScores: GameWithScores) {
        _game = gameWithScores.game
        _scores = gameWithScores.scores.toMutableList()
        _players = gameWithScores.players.toMutableList()

        _isGameFinished.value = gameWithScores.game.isGameFinished
        if (_game != null) currentGameId = game.gameId ?: throw RuntimeException("Game not found")
    }

    private fun restoreGameList() {
        val restoredGameList = mutableListOf<GameListItem>()
        if (scores.isEmpty()) return
        when (gameType) {
            THREE_PLAYER_GAME -> {
                restoredGameList.add(
                    gameFactory.createGameListItem( // first line
                        listOf(0, 0, 0),
                        listOf(
                            scores[0].playerOneCount,
                            scores[0].playerTwoCount,
                            scores[0].playerThreeCount
                        )
                    )
                )
                for (i in 1 until scores.size) {
                    val listOfCounts =
                        listOf(
                            scores[i].playerOneCount,
                            scores[i].playerTwoCount,
                            scores[i].playerThreeCount
                        )
                    val listOfPoints =
                        listOf(
                            scores[i].playerOneCount - scores[i - 1].playerOneCount,
                            scores[i].playerTwoCount - scores[i - 1].playerTwoCount,
                            scores[i].playerThreeCount - scores[i - 1].playerThreeCount
                        )

                    val item = gameFactory.createGameListItem(listOfPoints, listOfCounts)
                    restoredGameList.add(item)
                }
            }
            TWO_PLAYER_GAME -> {
                restoredGameList.add(
                    gameFactory.createGameListItem( // first line
                        listOf(0, 0),
                        listOf(scores[0].playerOneCount, scores[0].playerTwoCount)
                    )
                )
                for (i in 1 until scores.size) {
                    val listOfCounts =
                        listOf(
                            scores[i].playerOneCount,
                            scores[i].playerTwoCount,
                        )
                    val listOfPoints =
                        listOf(
                            scores[i].playerOneCount - scores[i - 1].playerOneCount,
                            scores[i].playerTwoCount - scores[i - 1].playerTwoCount,
                        )
                    val item = gameFactory.createGameListItem(listOfPoints, listOfCounts)
                    restoredGameList.add(item)
                }
            }
        }
        _gameList.value = restoredGameList.toMutableList()
    }

    private fun createMessage(res: Int, param: String = "") {
        MessageHolder.addMessage(
            MessageHolder.GAME_SCORE_LIST_DEST,
            String.format(App.getContext().getString(res), param)
        )
    }

    override fun onCleared() {
        super.onCleared()
        MessageHolder.scopeCancel()
    }

    companion object {
        const val PLAYER_ONE = 0
        const val PLAYER_TWO = 1
        const val PLAYER_THREE = 2

        const val NOBODY = -1

        const val ON_BARREL_VALUE = 880
        const val FINE_FOR_BARREL_OFF = -120
        const val OFF_BARREL_VALUE = 760
        const val WIN_VALUE = 1000
        const val RESET_TO_ZERO_VALUE = 555
        const val HUNDRED = 100
        const val WIN_THRESHOLD_ON_BARREL = 120
    }
}

data class SearchState(
    val startDate: Date = Date(0),
    val endDate: Date = Date(Long.MAX_VALUE),
    val name: String = ""
)