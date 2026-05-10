package com.example.bridgetranslator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageBottomSheet : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var languageManager: LanguageManager
    private var onLanguageSelected: ((Language) -> Unit)? = null

    companion object {
        private const val ARG_IS_SOURCE = "is_source"

        fun newInstance(isSource: Boolean): LanguageBottomSheet {
            return LanguageBottomSheet().apply {
                arguments = Bundle().apply { putBoolean(ARG_IS_SOURCE, isSource) }
            }
        }
    }

    fun setOnLanguageSelectedListener(listener: (Language) -> Unit) {
        onLanguageSelected = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_language, container, false)
        recyclerView = view.findViewById(R.id.rvLanguages)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isSource = arguments?.getBoolean(ARG_IS_SOURCE, true) ?: true
        languageManager = LanguageManager(requireContext())

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewLifecycleOwner.lifecycleScope.launch {
            val currentCode = if (isSource)
                languageManager.sourceLangCode.first()
            else
                languageManager.targetLangCode.first()

            recyclerView.adapter = LanguageAdapter(
                languages = Language.SUPPORTED_LANGUAGES,
                selectedCode = currentCode
            ) { selected ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (isSource) languageManager.setSourceLanguage(selected.code)
                    else languageManager.setTargetLanguage(selected.code)
                    onLanguageSelected?.invoke(selected)
                    dismiss()
                }
            }
        }
    }

    private inner class LanguageAdapter(
        private val languages: List<Language>,
        private var selectedCode: String,
        private val onItemSelected: (Language) -> Unit
    ) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvFlag: TextView = view.findViewById(R.id.tvFlag)
            val tvName: TextView = view.findViewById(R.id.tvName)
            val ivCheck: ImageView = view.findViewById(R.id.ivCheck)

            init {
                view.setOnClickListener { onItemSelected(languages[adapterPosition]) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lang = languages[position]
            holder.tvFlag.text = lang.flagEmoji
            holder.tvName.text = lang.name
            holder.ivCheck.visibility =
                if (lang.code == selectedCode) View.VISIBLE else View.INVISIBLE
        }

        override fun getItemCount() = languages.size
    }
}
