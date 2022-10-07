package com.nandaiqbalh.mypassword.presentation.ui.passwordlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.nandaiqbalh.mypassword.R
import com.nandaiqbalh.mypassword.data.local.database.entity.PasswordEntity
import com.nandaiqbalh.mypassword.databinding.ItemPasswordBinding
import com.nandaiqbalh.mypassword.utils.ColorGenerator
import com.nandaiqbalh.mypassword.utils.TextDrawable

class PasswordListAdapter(
    private val listener: PasswordItemClickListener
) :
    RecyclerView.Adapter<PasswordListAdapter.PasswordItemViewHolder>() {

    private var items: MutableList<PasswordEntity> = mutableListOf()

    fun setItems(items: List<PasswordEntity>) {
        clearItems()
        addItems(items)
        notifyDataSetChanged()
    }

    fun addItems(items: List<PasswordEntity>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordItemViewHolder {
        val binding =
            ItemPasswordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PasswordItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: PasswordItemViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int = items.size


    class PasswordItemViewHolder(
        private val binding: ItemPasswordBinding,
        private val listener: PasswordItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: PasswordEntity) {
            val menu = createPopupMenu(item)
            with(item) {
                binding.tvAppName.text = appName
                binding.tvIdentifier.text = username ?: email.orEmpty()
                binding.root.setOnClickListener { listener.onItemClicked(this) }
                binding.root.setOnLongClickListener {
                    listener.onItemLongClicked(this)
                    true
                }
                binding.ivIdentifierLetter.setImageDrawable(
                    TextDrawable.builder()
                        .beginConfig()
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRect(
                            (username ?: email)?.get(0).toString(),
                            ColorGenerator.MATERIAL.randomColor
                        )
                )
                binding.ivMenu.setOnClickListener {
                    menu.show()
                }
            }
        }

        private fun createPopupMenu(itemList: PasswordEntity): PopupMenu {
            return PopupMenu(itemView.context, binding.ivMenu).apply {
                menuInflater.inflate(R.menu.menu_password_action, this.menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_action_delete ->
                            listener.onDeleteMenuClicked(itemList)
                        R.id.menu_action_edit ->
                            listener.onEditMenuClicked(itemList)
                    }
                    true
                }
            }
        }
    }

}


interface PasswordItemClickListener {
    fun onItemClicked(item: PasswordEntity)
    fun onItemLongClicked(item: PasswordEntity)
    fun onDeleteMenuClicked(item: PasswordEntity)
    fun onEditMenuClicked(item: PasswordEntity)
}
