/*
 *  Copyright (c) 2022 Brayan Oliveira <brayandso.dev@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ichi2.anki.preferences

import androidx.preference.SwitchPreference
import anki.cards.card
import com.ichi2.anki.R
import com.ichi2.anki.snackbar.showSnackbar
import com.ichi2.preferences.NumberRangePreferenceCompat

class LowkeySettingsFragment : SettingsFragment() {
    override val preferenceResource: Int
        get() = R.xml.preferences_lowkey
    override val analyticsScreenNameConstant: String
        get() = "prefs.lowkey"

    override fun initSubscreen() {
        val col = col!!

        // Disable factor changes
        requirePreference<SwitchPreference>(R.string.lowkey_disable_factor_changes_key).apply {
            isChecked = col.get_config("lowkeyDisableFactorChanges", false)!!
            setOnPreferenceChangeListener { newValue ->
                col.set_config("lowkeyDisableFactorChanges", newValue)
            }
        }

        // Disable extra buttons
        requirePreference<SwitchPreference>(R.string.lowkey_disable_extra_buttons_key).apply {
            col.get_config("lowkeyDisableExtraButtons", false)?.apply {
                isChecked = this
            }
            setOnPreferenceChangeListener { newValue ->
                col.set_config("lowkeyDisableExtraButtons", newValue)
            }
        }

        // Reset factors button
        requirePreference<NumberRangePreferenceCompat>(R.string.lowkey_reset_cards_factor_key).apply {
            setOnPreferenceChangeListener { newValue ->
                val newFactor = newValue as Int * 10
                val cardsToUpdate = col.db
                    .queryLongList(
                        "SELECT id FROM cards WHERE factor != 0 AND factor != ?", newFactor
                    )
                cardsToUpdate.forEach {
                    val card = col.getCard(it)
                    card.factor = newFactor
                    card.flush()
                }
                showSnackbar("${cardsToUpdate.size} card factors successfully changed to $newValue%")
            }
        }
    }
}
