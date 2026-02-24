package dev.tiagosilva.whatsappclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dev.tiagosilva.whatsappclone.fragments.ChatsFragment
import dev.tiagosilva.whatsappclone.fragments.ContactFragment

class MainTabsAdapter(fm: FragmentManager):
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titles = arrayOf("CONVERSAS", "CONTATOS")
    override fun getCount(): Int = titles.size

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> ChatsFragment()
        else -> ContactFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}