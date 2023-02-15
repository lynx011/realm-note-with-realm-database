package com.example.realm_test.fragment
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realm_test.R
import com.example.realm_test.adapter.ArticleAdapter
import com.example.realm_test.databinding.FragmentArticleBinding
import com.example.realm_test.view_model.RealmViewModel
import com.google.android.material.textfield.TextInputEditText

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private lateinit var realmViewModel: RealmViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onStart() {
        super.onStart()
        realmViewModel.getArticle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realmViewModel = ViewModelProvider(this)[RealmViewModel::class.java]

        binding.rcView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            articleAdapter = ArticleAdapter()
            adapter = articleAdapter
            isVisible = true
        }

        realmViewModel.realmLiveData.observe(viewLifecycleOwner) { articles ->
            articleAdapter.differ.submitList(articles)
        }

        binding.saveBtn.setOnClickListener {
            val etTitle = binding.etTitle.text
            val etDesc = binding.etDesc.text

            if (etTitle.isNullOrEmpty()) {
                binding.titleLayout.error = "required"
                binding.titleLayout.requestFocus()
            } else {
                binding.titleLayout.error = null
            }
            if (etDesc.isNullOrEmpty()) {
                binding.descLayout.error = "required"
            } else {
                binding.descLayout.error = null
            }
            if (etTitle!!.isNotEmpty() && etDesc!!.isNotEmpty()) {
                realmViewModel.addArticle(title = etTitle.toString(), desc = etDesc.toString())
                realmViewModel.getArticle()
                binding.etTitle.error = null
                binding.etDesc.error = null
                Toast.makeText(requireContext(), "Successfully Saved", Toast.LENGTH_SHORT)
                    .show()
                etTitle.clear()
                etDesc.clear()
                binding.etTitle.clearFocus()
                binding.etDesc.clearFocus()
            } else {
                Toast.makeText(requireContext(), "fields are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val etTitle = binding.etTitle.text.toString()
                val etDesc = binding.etDesc.text.toString()
                if (etTitle.isNotEmpty()) {
                    binding.titleLayout.error = null
                }
                if (etDesc.isNotEmpty()) {
                    binding.descLayout.error = null
                }
            }

        }
        binding.etTitle.addTextChangedListener(textWatcher)
        binding.etDesc.addTextChangedListener(textWatcher)

        articleAdapter.onUpdateClick = { article ->
            val dialogView = LayoutInflater.from(context).inflate(R.layout.update_dialog_box, null)
            val etTitleDialog = dialogView.findViewById<TextInputEditText>(R.id.etTitleDialog)
            val etDescDialog = dialogView.findViewById<TextInputEditText>(R.id.etDescDialog)
            val dialogUpdateBtn = dialogView.findViewById<Button>(R.id.dialogUpdateBtn)
            etTitleDialog.setText(article.title.toString())
            etDescDialog.setText(article.description.toString())

            alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()
            alertDialog.show()

            dialogUpdateBtn.setOnClickListener {
                if (etTitleDialog.text!!.isNotEmpty() && etDescDialog.text!!.isNotEmpty()) {
                    val id = article.id
                    val etTitle = etTitleDialog.text
                    val etDesc = etDescDialog.text
                    realmViewModel.updateArticle(
                        id = id!!,
                        title = etTitle.toString(),
                        desc = etDesc.toString()
                    )
                    Toast.makeText(requireContext(), "Successfully Updated", Toast.LENGTH_SHORT)
                        .show()
                    realmViewModel.getArticle()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "fields are required", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        articleAdapter.onDeleteClick = { article ->
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.delete_24)
                .setMessage("Are you sure you want to delete?")
            dialog.setPositiveButton("Yes") { dLog, _ ->
                realmViewModel.deleteArticle(article.id!!)
                Toast.makeText(requireContext(), "deleted ${article.title}", Toast.LENGTH_SHORT)
                    .show()
                realmViewModel.getArticle()
                realmViewModel.realmLiveData.observe(viewLifecycleOwner) { article ->
                    articleAdapter.differ.submitList(article)
                }
                dLog.dismiss()
            }
            dialog.setNegativeButton("No") { dLog, _ ->
                dLog.dismiss()
            }
            dialog.create().show()
        }
    }

}