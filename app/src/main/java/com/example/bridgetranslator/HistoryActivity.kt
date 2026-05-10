package com.example.bridgetranslator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var dao: HistoryDao
    private lateinit var adapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView
    private lateinit var emptyState: View
    private lateinit var filterAll: TextView
    private lateinit var filterStarred: TextView

    private var allItems: List<HistoryEntity> = emptyList()
    private var showStarredOnly = false
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        applySystemInsets(topViewId = R.id.topBar, bottomNavId = R.id.bottomNav)

        dao = AppDatabase.get(this).historyDao()
        rvHistory = findViewById(R.id.rvHistory)
        emptyState = findViewById(R.id.emptyState)
        filterAll = findViewById(R.id.filterAll)
        filterStarred = findViewById(R.id.filterStarred)

        adapter = HistoryAdapter(
            onStar = { entry ->
                lifecycleScope.launch { dao.setStarred(entry.id, !entry.isStarred) }
            },
            onDelete = { entry ->
                lifecycleScope.launch { dao.delete(entry) }
            },
            onCopy = { text ->
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.setPrimaryClip(ClipData.newPlainText("translation", text))
                Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show()
            }
        )
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter

        // Search
        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString()?.trim() ?: ""
                applyFilter()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Filter tabs
        filterAll.setOnClickListener {
            showStarredOnly = false
            updateFilterTabs()
            applyFilter()
        }
        filterStarred.setOnClickListener {
            showStarredOnly = true
            updateFilterTabs()
            applyFilter()
        }

        // Clear all
        findViewById<View>(R.id.ivClearAll).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Delete all translation history?")
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch { dao.clearAll() }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Observe Room database - updates live on any change
        lifecycleScope.launch {
            dao.getAllFlow().collect { entries ->
                allItems = entries
                applyFilter()
            }
        }

        // Bottom Navigation
        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navTranslate).setOnClickListener {
            startActivity(Intent(this, TranslateActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navHistory).setOnClickListener { /* already here */ }
        findViewById<View>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun applyFilter() {
        var filtered = allItems
        if (showStarredOnly) filtered = filtered.filter { it.isStarred }
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.sourceText.contains(searchQuery, ignoreCase = true) ||
                it.resultText.contains(searchQuery, ignoreCase = true)
            }
        }
        val listItems = buildGroupedList(filtered)
        adapter.submitList(listItems)
        emptyState.visibility = if (listItems.isEmpty()) View.VISIBLE else View.GONE
        rvHistory.visibility = if (listItems.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateFilterTabs() {
        if (showStarredOnly) {
            filterStarred.setTextColor(getColor(R.color.white))
            filterStarred.setBackgroundColor(getColor(R.color.bubble_active))
            filterAll.setTextColor(getColor(R.color.text_secondary))
            filterAll.setBackgroundColor(getColor(R.color.search_bg))
        } else {
            filterAll.setTextColor(getColor(R.color.white))
            filterAll.setBackgroundColor(getColor(R.color.bubble_active))
            filterStarred.setTextColor(getColor(R.color.text_secondary))
            filterStarred.setBackgroundColor(getColor(R.color.search_bg))
        }
    }

    private fun buildGroupedList(entries: List<HistoryEntity>): List<ListItem> {
        if (entries.isEmpty()) return emptyList()

        val cal = Calendar.getInstance()
        val todayStart = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val yesterdayStart = todayStart - 86_400_000L

        val result = mutableListOf<ListItem>()
        var lastGroup = ""

        for (entry in entries) {
            val group = when {
                entry.timestamp >= todayStart -> "TODAY"
                entry.timestamp >= yesterdayStart -> "YESTERDAY"
                else -> "OLDER"
            }
            if (group != lastGroup) {
                result.add(ListItem.Header(group))
                lastGroup = group
            }
            result.add(ListItem.Entry(entry))
        }
        return result
    }
}

// - List item sealed class -

sealed class ListItem {
    data class Header(val label: String) : ListItem()
    data class Entry(val entity: HistoryEntity) : ListItem()
}

// - Adapter -

class HistoryAdapter(
    private val onStar: (HistoryEntity) -> Unit,
    private val onDelete: (HistoryEntity) -> Unit,
    private val onCopy: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = emptyList()

    fun submitList(list: List<ListItem>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is ListItem.Header -> 0
        is ListItem.Entry -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            val v = inflater.inflate(R.layout.item_history_header, parent, false)
            HeaderVH(v)
        } else {
            val v = inflater.inflate(R.layout.item_history_card, parent, false)
            EntryVH(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderVH).bind(item.label)
            is ListItem.Entry -> (holder as EntryVH).bind(item.entity)
        }
    }

    override fun getItemCount() = items.size

    inner class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(label: String) {
            (itemView as TextView).text = label
        }
    }

    inner class EntryVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvLangPair = view.findViewById<TextView>(R.id.tvLangPair)
        private val tvTime = view.findViewById<TextView>(R.id.tvTime)
        private val tvSource = view.findViewById<TextView>(R.id.tvSourceText)
        private val tvResult = view.findViewById<TextView>(R.id.tvResultText)
        private val ivStar = view.findViewById<ImageView>(R.id.ivStar)
        private val ivCopy = view.findViewById<ImageView>(R.id.ivCopy)
        private val ivDelete = view.findViewById<ImageView>(R.id.ivDelete)

        fun bind(entity: HistoryEntity) {
            val srcCode = entity.sourceLangCode.uppercase()
            val tgtCode = entity.targetLangCode.uppercase()
            tvLangPair.text = "$srcCode -> $tgtCode"
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(entity.timestamp))
            tvSource.text = entity.sourceText
            tvResult.text = entity.resultText

            val starColor = if (entity.isStarred) R.color.text_accent_blue else R.color.bottom_nav_icon
            ivStar.setColorFilter(itemView.context.getColor(starColor))

            ivStar.setOnClickListener { onStar(entity) }
            ivDelete.setOnClickListener { onDelete(entity) }
            ivCopy.setOnClickListener { onCopy(entity.resultText) }
        }
    }
}
