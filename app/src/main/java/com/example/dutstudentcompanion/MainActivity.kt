package com.example.dutstudentcompanion

import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dutstudentcompanion.ui.theme.DutStudentCompanionTheme
import java.text.SimpleDateFormat
import java.util.*

data class Mod(val name: String)
data class Asg(val title: String, var done: Boolean = false)
data class Alm(val title: String, val time: String)
data class Tst(val title: String)

@Composable
fun BoxCard(icon: String, num: String, label: String) {
    Card(Modifier.fillMaxWidth().height(90.dp), colors = CardDefaults.cardColors(Color(0xFF1E293B)), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(icon, fontSize = 20.sp)
            Text(num, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun Field(v: String, on: (String)->Unit, h: String) {
    OutlinedTextField(value = v, onValueChange = on, placeholder = { Text(h, color = Color.Gray, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DutStudentCompanionTheme {
                var page by remember { mutableStateOf("splash") }
                var fullName by remember { mutableStateOf("") }
                var dutEmail by remember { mutableStateOf("") }
                var tab by remember { mutableStateOf("Home") }
                val mods = remember { mutableStateListOf<Mod>() }
                val asgs = remember { mutableStateListOf<Asg>() }
                val alms = remember { mutableStateListOf<Alm>() }
                val tsts = remember { mutableStateListOf<Tst>() }

                if (page == "splash") {
                    Box(Modifier.fillMaxSize().background(Color(0xFF0099FF)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DUT", fontSize = 70.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("Student Companion", color = Color.White)
                            Spacer(Modifier.height(40.dp))
                            Button(onClick = { page = "login" }, modifier = Modifier.width(200.dp).height(48.dp), colors = ButtonDefaults.buttonColors(Color.White), shape = RoundedCornerShape(20.dp)) { Text("GET STARTED", color = Color(0xFF0099FF), fontWeight = FontWeight.Bold) }
                        }
                    }
                } else if (page == "login") {
                    var n by remember { mutableStateOf("") }; var s by remember { mutableStateOf("") }; var e by remember { mutableStateOf("") }; var p by remember { mutableStateOf("") }
                    Box(Modifier.fillMaxSize().background(Color(0xFF0099FF)), contentAlignment = Alignment.Center) {
                        Card(Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color(0xFF101A2E))) {
                            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Student Login", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Field(n, {n=it}, "Name"); Field(s, {s=it}, "Surname"); Field(e, {e=it}, "DUT Email"); Field(p, {p=it}, "Password")
                                Spacer(Modifier.height(10.dp))
                                Button(onClick = { if(n.isNotBlank() && e.isNotBlank()){ fullName="$n $s"; dutEmail=e; page="main" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFF0099FF))) { Text("LOGIN") }
                            }
                        }
                    }
                } else {
                    val ctx = LocalContext.current
                    // This checks every minute if it's time to ring - NO immediate ring
                    LaunchedEffect(alms.size) {
                        while(true) {
                            kotlinx.coroutines.delay(10000) // check every 10 sec
                            val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            val now2 = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            alms.forEach { alm ->
                                if (alm.time == now || alm.time == now2 || now.contains(alm.time) || alm.time.contains(now)) {
                                    try {
                                        val r = RingtoneManager.getRingtone(ctx, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                                        r?.play()
                                        Toast.makeText(ctx, "⏰ ${alm.title} - TIME NOW!", Toast.LENGTH_LONG).show()
                                    } catch(_: Exception){}
                                }
                            }
                        }
                    }

                    Scaffold(
                        topBar = { Box(Modifier.fillMaxWidth().background(Color(0xFF0099FF)).padding(12.dp)) { Text("Hello, $fullName 👋 | $dutEmail", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) } },
                        bottomBar = { NavigationBar(containerColor = Color(0xFF0B1220)) { listOf("Home","Modules","Assign","Alarms","Tests","Profile").forEach { t -> NavigationBarItem(selected = tab==t, onClick = {tab=t}, icon = {}, label = { Text(t, fontSize = 8.sp, color = if(tab==t) Color(0xFF0099FF) else Color.Gray) }) } } },
                        containerColor = Color(0xFF0B1220)
                    ) { pad ->
                        Box(Modifier.padding(pad).fillMaxSize().background(Color(0xFF0B1220)).padding(12.dp)) {
                            when(tab) {
                                "Home" -> LazyColumn { item {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Box(Modifier.weight(1f)){ BoxCard("📚","${mods.size}","Modules") }; Box(Modifier.weight(1f)){ BoxCard("⏳","${asgs.count{!it.done}}","Pending") } }
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Box(Modifier.weight(1f)){ BoxCard("✅","${asgs.count{it.done}}","Completed") }; Box(Modifier.weight(1f)){ BoxCard("👥","0","Projects") }; Box(Modifier.weight(1f)){ BoxCard("📄","${tsts.size}","Tests") } }
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Box(Modifier.weight(1f)){ BoxCard("🕰️","${alms.size}","Alarms") }; Box(Modifier.weight(1f)){ BoxCard("🔔","${alms.size}","Active") } }
                                    Spacer(Modifier.height(12.dp))
                                    Text("📌 Pending Tasks", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFF1E293B))) {
                                        if(asgs.count{!it.done}==0) Text("🎉 All done!", color = Color.Gray, modifier = Modifier.padding(14.dp), fontSize = 12.sp)
                                        else Column(Modifier.padding(6.dp)) { asgs.filter{!it.done}.forEach{ a -> Row(verticalAlignment = Alignment.CenterVertically){ Checkbox(checked = a.done, onCheckedChange = { a.done = it }); Text(a.title, color = Color.White, fontSize = 12.sp) } } }
                                    }
                                } }
                                "Alarms" -> {
                                    var title by remember { mutableStateOf("") }; var time by remember { mutableStateOf("") }
                                    Column {
                                        Text("⏰ Alarms for $fullName", color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Current time: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}", color = Color.Gray, fontSize = 12.sp)
                                        Field(title, {title=it}, "Title e.g. Study Maths")
                                        Field(time, {time=it}, "Time e.g. 14:30 or 02:30 PM")
                                        Text("✅ Will ring ONLY when clock reaches that time", color = Color(0xFFFF9500), fontSize = 11.sp, modifier = Modifier.padding(vertical = 6.dp))
                                        Button(onClick = { if(title.isNotBlank() && time.isNotBlank()){ alms.add(Alm(title,time)); Toast.makeText(ctx, "✅ Alarm set for $time", Toast.LENGTH_SHORT).show(); title=""; time="" } }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(Color(0xFFFF9500))) { Text("⏰ SET ALARM - RINGS AT TIME") }
                                        Spacer(Modifier.height(10.dp))
                                        LazyColumn { items(alms){ Text("⏰ ${it.title} - ${it.time} 🔔 Active", color = Color.White, modifier = Modifier.padding(6.dp)) } }
                                    }
                                }
                                "Modules" -> { var input by remember { mutableStateOf("") }; Column { Text("📚 Modules", color = Color.White, fontWeight = FontWeight.Bold); Field(input,{input=it},"Module name"); Button(onClick = { if(input.isNotBlank()){ mods.add(Mod(input)); input="" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFF0099FF))){ Text("ADD") }; LazyColumn { items(mods){ Text("📁 ${it.name}", color = Color.White, modifier = Modifier.padding(8.dp)) } } } }
                                "Assign" -> { var input by remember { mutableStateOf("") }; Column { Text("📝 Assignments", color = Color.White, fontWeight = FontWeight.Bold); Field(input,{input=it},"Title"); Button(onClick = { if(input.isNotBlank()){ asgs.add(Asg(input)); input="" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFF0099FF))){ Text("ADD") }; LazyColumn { items(asgs){ Row(verticalAlignment = Alignment.CenterVertically){ Checkbox(checked = it.done, onCheckedChange = { c -> it.done = c }); Text(it.title, color = Color.White) } } } } }
                                "Tests" -> { var input by remember { mutableStateOf("") }; Column { Text("✏️ Tests", color = Color.White, fontWeight = FontWeight.Bold); Field(input,{input=it},"Title"); Button(onClick = { if(input.isNotBlank()){ tsts.add(Tst(input)); input="" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFF0099FF))){ Text("ADD") }; LazyColumn { items(tsts){ Text("✏️ ${it.title}", color = Color.White, modifier = Modifier.padding(6.dp)) } } } }
                                "Profile" -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Box(Modifier.size(80.dp).background(Color(0xFF0099FF), CircleShape), contentAlignment = Alignment.Center){ Text(fullName.take(1).uppercase(), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold) }
                                        Spacer(Modifier.height(10.dp)); Text("Hello, $fullName! 👋", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text(dutEmail, color = Color(0xFF0099FF), fontSize = 13.sp)
                                        Spacer(Modifier.height(14.dp))
                                        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFF1E293B)), shape = RoundedCornerShape(14.dp)) {
                                            Column(Modifier.padding(14.dp)) {
                                                Text("📚 Modules: ${mods.size}", color = Color.White, modifier = Modifier.padding(4.dp))
                                                Text("⏳ Pending: ${asgs.count{!it.done}}", color = Color.White, modifier = Modifier.padding(4.dp))
                                                Text("✅ Completed: ${asgs.count{it.done}}", color = Color.White, modifier = Modifier.padding(4.dp))
                                                Text("👥 Projects: 0", color = Color.White, modifier = Modifier.padding(4.dp))
                                                Text("✏️ Tests: ${tsts.size}", color = Color.White, modifier = Modifier.padding(4.dp))
                                                Text("⏰ Alarms: ${alms.size} (🔔 ${alms.size} active)", color = Color.White, modifier = Modifier.padding(4.dp))
                                            }
                                        }
                                        Spacer(Modifier.height(16.dp))
                                        Button(onClick = { page="login" }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(Color(0xFFE74C3C)), shape = RoundedCornerShape(20.dp)){ Text("🚪 LOGOUT") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}