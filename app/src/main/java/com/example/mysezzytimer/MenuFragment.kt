package com.example.mysezzytimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sweaty_shredder_menu.setOnClickListener(this)
        toning_power_menu.setOnClickListener(this)
        blaster_menu.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            sweaty_shredder_menu -> {
                Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                    .navigate(R.id.action_menuFragment_to_sweatyShredderTimerFragment)
            }
            toning_power_menu -> {
                Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                    .navigate(R.id.action_menuFragment_to_toningPowerFragment)
            }
            blaster_menu -> {
                Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                    .navigate(R.id.action_menuFragment_to_blasterFragment)
            }
        }
    }
}