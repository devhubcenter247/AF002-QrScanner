package com.azg.alf_002_qrscan.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.azg.alf_002_qrscan.R
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDate.withDayOfWeek(dayOfWeek: Int): LocalDate {
    return this.minusDays(this.dayOfWeek.value.toLong() - dayOfWeek)
}

fun Calendar.formatTime(): String {
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    return format.format(this.time)
}

fun String.convertTimeToDate(): String {
    val timestamp = this.toLongOrNull() ?: System.currentTimeMillis()
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
    return format.format(date)
}

enum class DateTimePickerMode { Date, Time, DateTime }

data class DateTimeOption(
    val minYear: Int = 1900,
    val maxYear: Int = 2100,
    val minDate: LocalDate? = null,
    val maxDate: LocalDate? = null,
    val is24Hour: Boolean = true,
    val minuteInterval: Int = 1,
    val mode: DateTimePickerMode = DateTimePickerMode.DateTime,
    val title: String = "Select Date & Time",
    val dateLabel: String = "Date",
    val timeLabel: String = "Time",
    val cancelText: String = "Cancel",
    val hourLabel: String = "Hour",
    val minuteLabel: String = "Minute",
    val monthLabel: String = "Month",
    val yearLabel: String = "Year",
    val okText: String = "OK",
)
@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    options: DateTimeOption = DateTimeOption()
) {
    var selectedDate by remember { mutableStateOf(initialDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(initialDateTime.toLocalTime()) }
    var showDatePicker by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        options.minDate?.let { min ->
            if (selectedDate.isBefore(min)) selectedDate = min
        }
        options.maxDate?.let { max ->
            if (selectedDate.isAfter(max)) selectedDate = max
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = options.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                if (options.mode == DateTimePickerMode.DateTime) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                MaterialTheme.shapes.small
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DateTimeTab(
                            text = options.dateLabel,
                            isSelected = showDatePicker,
                            onClick = { showDatePicker = true }
                        )
                        DateTimeTab(
                            text = options.timeLabel,
                            isSelected = !showDatePicker,
                            onClick = { showDatePicker = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
                when (options.mode) {
                    DateTimePickerMode.Date -> {
                        CustomDatePicker(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            minYear = options.minYear,
                            maxYear = options.maxYear,
                            minDate = options.minDate,
                            maxDate = options.maxDate
                        )
                    }
                    DateTimePickerMode.Time -> {
                        CustomTimePicker(
                            selectedTime = selectedTime,
                            onTimeSelected = { selectedTime = it },
                            is24Hour = options.is24Hour,
                            minuteInterval = options.minuteInterval,
                            hourLabel = options.hourLabel,
                            minuteLabel = options.minuteLabel
                        )
                    }
                    DateTimePickerMode.DateTime -> {
                        if (showDatePicker) {
                            CustomDatePicker(
                                selectedDate = selectedDate,
                                onDateSelected = { selectedDate = it },
                                minYear = options.minYear,
                                maxYear = options.maxYear,
                                minDate = options.minDate,
                                maxDate = options.maxDate,
                            )
                        } else {
                            CustomTimePicker(
                                selectedTime = selectedTime,
                                onTimeSelected = { selectedTime = it },
                                is24Hour = options.is24Hour,
                                minuteInterval = options.minuteInterval,
                                hourLabel = options.hourLabel,
                                minuteLabel = options.minuteLabel
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                if (options.mode == DateTimePickerMode.DateTime) {
                    SelectedDateTimePreview(
                        date = selectedDate,
                        time = selectedTime
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(options.cancelText)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val result = when (options.mode) {
                                DateTimePickerMode.Date,
                                DateTimePickerMode.DateTime ->
                                    LocalDateTime.of(selectedDate, selectedTime)
                                DateTimePickerMode.Time ->
                                    LocalDateTime.of(LocalDate.now(), selectedTime)
                            }
                            onDateTimeSelected(result)
                        }
                    ) {
                        Text(options.okText)
                    }
                }
            }
        }
    }
}
@Composable
private fun RowScope.DateTimeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(4.dp)
            .background(backgroundColor, MaterialTheme.shapes.small)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
@Composable
private fun SelectedDateTimePreview(
    date: LocalDate,
    time: LocalTime
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d, yyyy 'at' h:mm a") }
    val dateTime = LocalDateTime.of(date, time)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.selected),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = dateTime.format(formatter),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun clampDate(
    year: Int,
    month: Month,
    baseDay: Int,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
): LocalDate {
    val ym = YearMonth.of(year, month)
    val safeDay = baseDay.coerceIn(1, ym.lengthOfMonth())
    var result = LocalDate.of(year, month, safeDay)

    minDate?.let { if (result.isBefore(it)) result = it }
    maxDate?.let { if (result.isAfter(it)) result = it }

    return result
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    minYear: Int = 1900,
    maxYear: Int = 2100,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    monthLabel: String = "Month",
    yearLabel: String = "Year",
) {
    val years = remember(minYear, maxYear) { (minYear..maxYear).toList() }
    val months = remember { Month.entries }
    var selectedYear by remember(selectedDate.year, minYear, maxYear) {
        mutableStateOf(selectedDate.year.coerceIn(minYear, maxYear))
    }
    var selectedMonth by remember(selectedDate.month) {
        mutableStateOf(selectedDate.month)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedYear.toString(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text(yearLabel) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        modifier = Modifier.heightIn(max = 250.dp),
                        shape = RoundedCornerShape(12.dp),
                        onDismissRequest = { expanded = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    selectedYear = year
                                    expanded = false
                                    val newDate = clampDate(
                                        year = selectedYear,
                                        month = selectedMonth,
                                        baseDay = selectedDate.dayOfMonth,
                                        minDate = minDate,
                                        maxDate = maxDate
                                    )
                                    onDateSelected(newDate)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMonth.getDisplayName(
                            TextStyle.FULL,
                            LocalLocale.current.platformLocale
                        ),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text(monthLabel) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        modifier = Modifier.heightIn(max = 250.dp),
                        shape = RoundedCornerShape(12.dp),
                        onDismissRequest = { expanded = false }
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        month.getDisplayName(
                                            TextStyle.FULL,
                                            LocalLocale.current.platformLocale
                                        )
                                    )
                                },
                                onClick = {
                                    selectedMonth = month
                                    expanded = false
                                    val newDate = clampDate(
                                        year = selectedYear,
                                        month = selectedMonth,
                                        baseDay = selectedDate.dayOfMonth,
                                        minDate = minDate,
                                        maxDate = maxDate
                                    )
                                    onDateSelected(newDate)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        val daysInMonth = remember(selectedYear, selectedMonth) {
            YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
        }
        val firstDayOfMonth = remember(selectedYear, selectedMonth) {
            LocalDate.of(selectedYear, selectedMonth, 1).dayOfWeek
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(336.dp)
        ) {
            items(DayOfWeek.entries) { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, LocalLocale.current.platformLocale),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
            }
            items(firstDayOfMonth.value - 1) {
                Spacer(modifier = Modifier.size(48.dp))
            }
            val listDay = (1..daysInMonth).toList()

            items(listDay) { day ->
                val date = LocalDate.of(selectedYear, selectedMonth, day)
                val isDisabled =
                    (minDate != null && date.isBefore(minDate)) ||
                            (maxDate != null && date.isAfter(maxDate))
                val isSelected = selectedDate == date
                val isToday = date == LocalDate.now()
                val bgColor =
                    when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else -> Color.Transparent
                    }
                val textColor =
                    when {
                        isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(2.dp)
                        .background(
                            color = if (isDisabled) Color.Transparent else bgColor,
                            shape = CircleShape
                        )
                        .let {
                            if (!isDisabled) {
                                it.clickable { onDateSelected(date) }
                            } else {
                                it
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
@Composable
private fun CustomTimePicker(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    is24Hour: Boolean = true,
    minuteInterval: Int = 1,
    hourLabel: String = "Hour",
    minuteLabel: String = "Minute",
    allowFormatToggle: Boolean = true,
) {
    val safeInterval = minuteInterval.coerceIn(1, 30)
    var show24HourFormat by remember(is24Hour) { mutableStateOf(is24Hour) }
    val minuteItems = remember(safeInterval) {
        (0..59 step safeInterval).toList()
    }
    val normalizedMinute = remember(selectedTime.minute, safeInterval) {
        val base = (selectedTime.minute / safeInterval) * safeInterval
        base.coerceIn(0, 59)
    }

    Column {
        if (allowFormatToggle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilterChip(
                    selected = show24HourFormat,
                    onClick = { show24HourFormat = !show24HourFormat },
                    label = { Text(stringResource(R.string.format_24h)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hourItems = if (show24HourFormat) {
                (0..23).toList()
            } else {
                (1..12).toList()
            }
            val displayHour = if (show24HourFormat) {
                selectedTime.hour
            } else {
                val h = selectedTime.hour
                when (h) {
                    0 -> 12
                    in 1..12 -> h
                    else -> h - 12
                }
            }

            TimeWheel(
                items = hourItems,
                selectedItem = displayHour,
                onItemSelected = { newHour ->
                    val hour = if (show24HourFormat) {
                        newHour
                    } else {
                        val isPm = selectedTime.hour >= 12
                        when {
                            isPm && newHour == 12 -> 12
                            isPm -> newHour + 12
                            !isPm && newHour == 12 -> 0
                            else -> newHour
                        }
                    }
                    onTimeSelected(
                        selectedTime
                            .withHour(hour)
                            .withMinute(normalizedMinute)
                    )
                },
                label = hourLabel
            )

            Text(
                text = ":",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            TimeWheel(
                items = minuteItems,
                selectedItem = normalizedMinute,
                onItemSelected = { newMinute ->
                    onTimeSelected(
                        selectedTime.withMinute(newMinute)
                    )
                },
                label = minuteLabel
            )
            if (!show24HourFormat) {
                Spacer(modifier = Modifier.width(16.dp))

                TimeWheel(
                    items = listOf("AM", "PM"),
                    selectedItem = if (selectedTime.hour < 12) "AM" else "PM",
                    onItemSelected = { period ->
                        val curHour = selectedTime.hour
                        val newHour = when (period) {
                            "AM" -> if (curHour >= 12) curHour - 12 else curHour
                            "PM" -> if (curHour < 12) curHour + 12 else curHour
                            else -> curHour
                        }
                        onTimeSelected(
                            selectedTime
                                .withHour(newHour)
                                .withMinute(normalizedMinute)
                        )
                    },
                    label = ""
                )
            }
        }
    }
}
@Composable
private fun <T> TimeWheel(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .height(120.dp)
                .width(60.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    val isSelected = item == selectedItem
                    Box(
                        modifier = Modifier
                            .size(48.dp, 40.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { onItemSelected(item) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.toString().padStart(2, '0'),
                            style = if (isSelected) MaterialTheme.typography.titleMedium
                            else MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}