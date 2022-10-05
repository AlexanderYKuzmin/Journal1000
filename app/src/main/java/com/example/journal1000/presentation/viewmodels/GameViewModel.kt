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

    //private val contentRules = MarkdownParser.parse(DataHolder.rulesText)

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

    /*private var _games: MutableLiveData<MutableList<GameWithScores>> = MutableLiveData()
    val games: LiveData<MutableList<GameWithScores>>
        get() = _games*/

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

    private val getListOfGamesUseCase = GetListOfGamesUseCase(repository)
    private val getGameWithScoresUseCase = GetGameWithScoresUseCase(repository)
    //private val saveGame = SaveGameUseCase(repository)
    //private val savePlayersUseCase = SavePlayersUseCase(repository)
    private val saveGameWithScoresUseCase = SaveGameWithScoresUseCase(repository)
    private val deleteAllGamesUseCase = GetGameWithScoresUseCase(repository)
    //private val deleteGameWithScoresUseCase = DeleteGameWithScoresUseCase(repository)

    init {
        Log.d("Init Block", "Block started")
        loadPreferences()
    }

    fun startGame() {
        _isGameOn.value = true
    }

    /*@OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onGameReady() {
        gameSettingsStarted.value = true
    }*/

    //fun loadGames() = getListOfGamesUseCase.getListOfGames()

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
                Log.d("VM Save Game", "[${Thread.currentThread().name}]")
            }
            job.join()
            if (id != -1L && id != currentGameId) {
                Log.d("VM save game", "game saved? starting setup gameId")
                _game?.let { game ->
                    game.gameId = id
                    _players.map { it.gameId = id }
                    _scores.map { it.gameId = id }
                }
                currentGameId = id
                savePreferences()
            }
        }
        Log.d("VM Save Game", "currentGameId = $currentGameId")
        Log.d("VM Save Game", "game_id = ${game.gameId}")
        players.forEach { Log.d("VM Save Game", "player ${it.name} game_id = ${it.gameId}") }
        scores.forEach { Log.d("VM Save Game", "score ${it.step} game_id = ${it.gameId}") }
    }

    /*fun removeGame(games: List<Game>) {
        deleteGameWithScoresUseCase.deleteGameWithScores(games)
    }*/

    fun createNewGame(playersNames: List<String>) {
        Log.d("Create Game", "New game creation")
        _game = gameFactory.getNewGameInstance(playersNames.size)
        Log.d("VM Create Game", "Game created game_id = ${game.gameId}")
        for (i in playersNames.indices) {
            PlayerFactory.getNewPlayerInstance(playersNames[i], i)
                .also {
                    _players.add(it)
                }
        }
        Log.d("VM Create Game", "Players created size = ${players.size}")
        _players[PLAYER_ONE].onHundred = true

        saveGame()
        startGame()
    }

    fun handleRules() {
        _showRules.value = Unit
    }

    fun handleNew() {
        Log.d("GameVM", "Start new")
        clearGameData()
        loadPreferences()
        startNewGame()
    }

    fun handleRestartPreparation() {
        savePreferences()
        clearGameData()
    }

    fun handleSave() {
        Log.d("VM handle save() ", "saving game...")
        _game?.let {
            saveGame()
        }
    }

    fun handleSearchPanelOnShow() {
        isSearchShow = !isSearch.value!!
        _isSearch.value = isSearchShow
    }

    /*fun handleSearchResult(startDate: String?, endDate: String?) {
        _searchState.value = if (searchState.value == null) SearchState()
        else searchState.value!!.copy(
            startDate = startDate?.toDate() ?: Date(0),
            endDate = endDate?.toDate() ?: Date(Long.MAX_VALUE)
        )
        Log.d("SearchResult", "Awaiting for database request answer")
        *//*viewModelScope.launch {
            _games.value = getListOfGamesUseCase.getListOfGames(
                searchState.value!!.startDate,
                searchState.value!!.endDate
            )
        }*//*
    }*/

    fun handleSelectGame(gameWithScores: GameWithScores) {
        restoreGameVariables(gameWithScores)
        restoreGameList()
        startGame()
        savePreferences()
    }

    fun handleCount(points: List<Int>) {
        Log.d("handleCount", "handle count started")
        Log.d("handleCount", "points size = ${points.size}")

        MessageHolder.clear(MessageHolder.GAME_SCORE_LIST_DEST)
        prevPlayers = players.map { player -> player.copy() }.toList()
        clearAuctionData()
        _isBackStepPressed.value = false

        //val pointsInt: List<Int> = points.filter { it.isNotBlank() }.map { it.toInt() }
        val roundedPoints = points.map { (round(it.toDouble() / 5) * 5).toInt() }
        //val playersCounts = mutableListOf<Int>()

        changePlayerOnHundredNumber()

        for (i in points.indices) {
            _players[i].count += roundedPoints[i]
            _players[i].countInTime = roundedPoints[i]
            Log.d("handleCount", "players[$i] = ${players[i].count}")
            checkPlayerForBolt(players[i], roundedPoints[i])
            checkPlayerForBarrel(players[i])
            checkPlayerForResetCount(players[i])
            checkForPlayerPointOverflow(players[i])

            _players[i].onHundred = i == game.onHundredPlayer.value
            Log.d("handle count", "players[$i].onHundred = ${players[i].onHundred}")

        }
        reCheckBarrelDataOfPlayers()
        val playersCounts = players.map { it.count }.toMutableList()

        currentScore = gameFactory.getScoreInstance(playersCounts).also { _scores.add(it) }

        val newGameListItem = gameFactory.createGameListItem(roundedPoints, playersCounts)
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
        Log.d("Handle cancel count", "current gamelist size = ${list.size}")
        list.removeLast()
        Log.d("Handle cancel count", "current gamelist size after removal last = ${list.size}")
        _gameList.value = list

        _isGameFinished.value = false
        _isBackStepPressed.value = true
        //playerOnHundred = game.onHundredPlayer.value
    }

    @SuppressWarnings("SuspiciousIndentation")
    fun handleRequestPoints(auctionData: Pair<PlayerOrder, Int>) {
        clearAuctionData()
        _players.map {
            if (it.playerOrder == auctionData.first) {
            it.requestedPoints = auctionData.second
                Log.d("Game VM", "handle request points player name = ${it.name} playerOrder = ${it.playerOrder} requested points = ${it.requestedPoints}")
            _auctionFinished.value = Unit
            }
        }
    }

    private fun setDefaultRequestPoints() {
        _players.map {
            if (it.isOnBarrel) {
                it.requestedPoints = 120
                return
            }
        }
        _players.map { if (it.onHundred) it.requestedPoints = 100 }
    }

    fun handleLastGameLoading() {
        loadGame(prefs.id)
    }

    fun clearAuctionData() {
        _players.map { it.requestedPoints = 0 }
        players.forEach { Log.d("VM clear data", "requested points player[${it.playerOrder.value}] = ${it.requestedPoints}") }
    }

    private fun finishGame() {
        _game?.isGameFinished = true
        _isGameFinished.value = true
        _isGameOn.value = false
    }

    private fun startNewGame() {
        _gameSettingsStarted.value = Unit
    }

    private fun checkPlayerForBolt(player: Player, points: Int) {
        if (points == 0) {
            if (!player.isOnBarrel) {
                player.boltNumber++
                createMessage(R.string.player_get_bolt, player.name)
            }
        }
        if (player.boltNumber == 3) {
            player.count -= 120
            player.boltNumber = 0
            createMessage(R.string.player_pay_fine_bolt, player.name)
        }
        // add notification ???
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
                    Log.d("VM check player for barrel", "Player getting off ${player.name}, ${player.onBarrelAttemptCount}, ${player.playerOrder}, and On Barrel is $onBarrel")
                    getOffFromBarrel(player)
                } else player.count = ON_BARREL_VALUE
            } else { // если не на бочке но набрал >= 880
                player.isOnBarrel = true
                player.count = ON_BARREL_VALUE
                onBarrel = player.playerOrder
                createMessage(R.string.player_on_barrel, player.name)
            }
        }
    }

    private fun reCheckBarrelDataOfPlayers() {
        players.forEach{ player ->
            if (player.playerOrder != onBarrel && player.count >= ON_BARREL_VALUE && !player.isWinner) {
                Log.d("VM recheckBarel", "Player ${player.name} getting off after recheck")
                getOffFromBarrel(player)
            }
        }
        if (!players.any { it.isOnBarrel }) onBarrel = null
    }

    private fun getOffFromBarrel(player: Player) {
        player.count = OFF_BARREL_VALUE
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
        Log.d("Load pref", "original id = $currentGameId")
        viewModelScope.launch {
            prefs = prefManager.readData(GAME_ID, NUM_OF_PLAYERS, NAME_ONE, NAME_TWO, NAME_THREE)
        }

        Log.d("Load pref", "current id = $currentGameId")
    }

    private fun restoreGameVariables(gameWithScores: GameWithScores) {
        _game = gameWithScores.game
        _scores = gameWithScores.scores.toMutableList()
        _players = gameWithScores.players.toMutableList()

        //_gameType = gameWithScores.game.type !!!Обратить внимание , что не глючит
        _isGameFinished.value = gameWithScores.game.isGameFinished
        //playerOnHundred = game.onHundredPlayer.value
        Log.d("VM restore variables", "player on hundred = ${game.onHundredPlayer.value}")
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
        const val OFF_BARREL_VALUE = 760
        const val WIN_VALUE = 1000
        const val RESET_TO_ZERO_VALUE = 555
    }
}

data class SearchState(
    val startDate: Date = Date(0),
    val endDate: Date = Date(Long.MAX_VALUE),
    val name: String = ""
)