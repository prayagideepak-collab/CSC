package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.FamilyMember
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun RationCardValidationScreen(
    declaredAadhaarCount: Int,
    onUpdateDeclaredCount: (Int) -> Unit,
    familyMembers: List<FamilyMember>,
    onAddMember: (FamilyMember) -> Unit,
    onUpdateMember: (Int, FamilyMember) -> Unit,
    onRemoveMember: (Int) -> Unit,
    twinConfirmed: Boolean,
    onToggleTwinConfirmed: (Boolean) -> Unit
) {
    val actualCount = familyMembers.size
    val countMismatch = actualCount != declaredAadhaarCount

    // Validation checks
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val warnings = mutableListOf<String>()
    if (countMismatch) {
        warnings.add("Warning: Total family member count ($actualCount) does not match declared Aadhaar count ($declaredAadhaarCount)!")
    }

    val under5Members = mutableListOf<String>()
    val dobMap = mutableMapOf<LocalDate, MutableList<String>>()

    familyMembers.forEach { member ->
        val dobParsed = try { LocalDate.parse(member.dob, formatter) } catch (e: Exception) { null }
        if (dobParsed != null) {
            val age = ChronoUnit.YEARS.between(dobParsed, today)
            if (age < 5) {
                under5Members.add(member.name)
            }
            dobMap.getOrPut(dobParsed) { mutableListOf() }.add(member.name)
        }
    }

    if (under5Members.isNotEmpty()) {
        warnings.add("Exclusion Warning: Members under 5 years (${under5Members.joinToString()}) will NOT be added to ration list.")
    }

    // Duplicate DOB check / Twin check
    var hasTwinConflict = false
    dobMap.forEach { (dob, names) ->
        if (names.size > 1 && !twinConfirmed) {
            hasTwinConflict = true
            warnings.add("Duplicate DOB conflict detected for ${names.joinToString()} on $dob. Check 'Yes, Twin' confirmation if twins.")
        }
    }

    // Non-twin 9 months (270 days) gap check
    val sortedDates = dobMap.keys.sorted()
    for (i in 0 until sortedDates.size - 1) {
        val d1 = sortedDates[i]
        val d2 = sortedDates[i+1]
        val daysBetween = ChronoUnit.DAYS.between(d1, d2)
        if (daysBetween in 1..269 && dobMap[d1]?.size == 1 && dobMap[d2]?.size == 1) {
            warnings.add("Birth Gap Warning: Only $daysBetween days between birth dates $d1 and $d2. Non-twin members must have at least 9 months (270 days) gap.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Ration Card Family Member Validation Engine",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Validates Aadhaar count match, age < 5 exclusion, twin verification, and 270-day non-twin gap enforcement.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Declared Aadhaar Count:", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = declaredAadhaarCount.toString(),
                        onValueChange = { onUpdateDeclaredCount(it.toIntOrNull() ?: 0) },
                        modifier = Modifier
                            .width(80.dp)
                            .testTag("declared_aadhaar_input"),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Actual Added Members: $actualCount", style = MaterialTheme.typography.bodySmall)

                if (hasTwinConflict) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = twinConfirmed,
                            onCheckedChange = onToggleTwinConfirmed,
                            modifier = Modifier.testTag("twin_confirmed_checkbox")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yes, Twin (Confirm duplicate birth dates)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Warnings box
        if (warnings.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = Color(0xFFC62828))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Validation Alerts (${warnings.size})", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    warnings.forEach { w ->
                        Text("• $w", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB71C1C))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Family Members List Header & Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Family Members List (3-field validation)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Button(
                onClick = {
                    onAddMember(FamilyMember(name = "New Member", dob = "2020-01-01", gender = "Male"))
                },
                modifier = Modifier.testTag("add_member_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Member")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(familyMembers) { index, member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Member #${index + 1}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { onRemoveMember(index) }, modifier = Modifier.testTag("remove_member_$index")) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        // 3-field demographic validation per member (Name, DOB, Gender)
                        OutlinedTextField(
                            value = member.name,
                            onValueChange = { onUpdateMember(index, member.copy(name = it)) },
                            label = { Text("Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("member_name_$index"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = member.dob,
                                onValueChange = { onUpdateMember(index, member.copy(dob = it)) },
                                label = { Text("DOB (YYYY-MM-DD)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("member_dob_$index"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = member.gender,
                                onValueChange = { onUpdateMember(index, member.copy(gender = it)) },
                                label = { Text("Gender") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("member_gender_$index"),
                                singleLine = true
                            )
                        }
                    }
                }
            }
        }
    }
}
