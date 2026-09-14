package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.util.JalaliDate
import com.vitran.shop.ui.util.JalaliMonthNames
import com.vitran.shop.ui.util.daysInJalaliMonth
import com.vitran.shop.ui.util.formatIsoDateAsJalali
import com.vitran.shop.ui.util.gregorianToJalali
import com.vitran.shop.ui.util.jalaliToGregorian
import com.vitran.shop.ui.util.jalaliToIsoDate
import com.vitran.shop.ui.util.parseIsoDateToJalali
import com.vitran.shop.ui.util.toPersianDigits
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_birthday_confirm
import vitranshop.shared.generated.resources.account_birthday_placeholder
import vitranshop.shared.generated.resources.account_cancel
import vitranshop.shared.generated.resources.account_field_birthday
import vitranshop.shared.generated.resources.ic_calendar
import kotlin.time.Clock

/**
 * Read-only birthday field that opens a Jalali calendar dialog.
 * [birthdayIso] is Gregorian `yyyy-MM-dd` or blank/null when unset.
 */
@Composable
internal fun AccountBirthdayField(
    birthdayIso: String?,
    onBirthdayChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }
    val display = birthdayIso
        ?.takeIf { it.isNotBlank() }
        ?.let { formatIsoDateAsJalali(it) }
        .orEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { open = true },
    ) {
        AccountStackedField(
            label = stringResource(Res.string.account_field_birthday),
            value = display,
            onValueChange = {},
            placeholder = stringResource(Res.string.account_birthday_placeholder),
            readOnly = true,
            trailing = {
                AccountTrailingIcon(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = stringResource(Res.string.account_field_birthday),
                )
            },
        )
    }

    if (open) {
        JalaliBirthdayPickerDialog(
            initialIso = birthdayIso,
            onDismiss = { open = false },
            onConfirm = { iso ->
                onBirthdayChange(iso)
                open = false
            },
        )
    }
}

@Composable
private fun JalaliBirthdayPickerDialog(
    initialIso: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val today = remember {
        @Suppress("DEPRECATION")
        val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        @Suppress("DEPRECATION")
        gregorianToJalali(local.year, local.monthNumber, local.dayOfMonth)
    }
    val initial = remember(initialIso) {
        initialIso?.takeIf { it.isNotBlank() }?.let { parseIsoDateToJalali(it) } ?: today
    }
    var viewYear by remember(initial) { mutableIntStateOf(initial.year) }
    var viewMonth by remember(initial) { mutableIntStateOf(initial.month) }
    var selectedDay by remember(initial) { mutableIntStateOf(initial.day) }

    val daysInMonth = daysInJalaliMonth(viewYear, viewMonth)
    val clampedDay = selectedDay.coerceAtMost(daysInMonth)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            VitranText(
                text = stringResource(Res.string.account_field_birthday),
                style = VitranTextStyle.Title,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = {
                            if (viewMonth == 1) {
                                viewMonth = 12
                                viewYear -= 1
                            } else {
                                viewMonth -= 1
                            }
                        },
                    ) {
                        Text(
                            text = "‹",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    Text(
                        text = toPersianDigits("${JalaliMonthNames[viewMonth - 1]} $viewYear"),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    TextButton(
                        onClick = {
                            if (viewMonth == 12) {
                                viewMonth = 1
                                viewYear += 1
                            } else {
                                viewMonth += 1
                            }
                        },
                    ) {
                        Text(
                            text = "›",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                val dayOffset = jalaliWeekdayOffset(viewYear, viewMonth)
                val cells = dayOffset + daysInMonth
                val rows = (cells + 6) / 7
                Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs)) {
                    repeat(rows) { row ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            repeat(7) { col ->
                                val index = row * 7 + col
                                val day = index - dayOffset + 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (day in 1..daysInMonth) {
                                        val selected = day == clampedDay
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(
                                                    if (selected) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.surface
                                                    },
                                                )
                                                .clickable(role = Role.Button) {
                                                    selectedDay = day
                                                }
                                                .padding(VitranSpacing.xs),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = toPersianDigits(day.toString()),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (selected) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                },
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val iso = jalaliToIsoDate(JalaliDate(viewYear, viewMonth, clampedDay))
                    onConfirm(iso)
                },
            ) {
                Text(
                    text = stringResource(Res.string.account_birthday_confirm),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.account_cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        shape = RoundedCornerShape(VitranRadius.large),
    )
}

/**
 * Saturday-first weekday offset for the 1st of the Jalali month.
 * kotlinx DayOfWeek: Monday=0 … Sunday=6 → Saturday-first index via +2.
 */
private fun jalaliWeekdayOffset(year: Int, month: Int): Int {
    val (gy, gm, gd) = jalaliToGregorian(year, month, 1)
    val mondayBased = LocalDate(gy, gm, gd).dayOfWeek.ordinal
    return (mondayBased + 2) % 7
}
