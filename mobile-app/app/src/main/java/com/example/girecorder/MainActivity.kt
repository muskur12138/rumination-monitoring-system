package com.example.girecorder

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// =====================================================================
//  GI-MONITOR 手机监控 APP —— 界面骨架（核心 BLE 逻辑未公开）
//  仅保留界面结构，扫描 / 连接 / 状态解析等核心逻辑已省略。
// =====================================================================

class MainActivity : ComponentActivity() {

    private fun requestPermissions() {
        val perms = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perms.add(Manifest.permission.BLUETOOTH_SCAN)
            perms.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        val missing = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            MaterialTheme {
                MonitorScreen()
            }
        }
    }
}

// 每台设备在界面上的状态
data class DeviceUi(
    val address: String,
    val name: String,
    val nickname: String? = null,
    val state: String = "未连接",
    val status: String = "",
    val connected: Boolean = false
)

@Composable
fun MonitorScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("gimonitor", Context.MODE_PRIVATE) }

    // 核心 BLE 相关对象（扫描器、GATT 连接等）已省略
    val devices = remember { mutableStateMapOf<String, DeviceUi>() }

    var scanning by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf<String?>(null) }

    fun loadNickname(addr: String): String? = prefs.getString("nick_$addr", null)

    // 核心：BLE 扫描、连接、状态解析逻辑在此省略（未公开）

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("GI 监控（一对多）", style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { scanning = !scanning }) {
                Text(if (scanning) "停止扫描" else "扫描设备")
            }
            Button(onClick = { /* 核心：连接全部 */ }) { Text("连接全部") }
            Button(onClick = { /* 核心：断开全部 */ }) { Text("断开全部") }
        }

        val list = devices.values.toList()
        val connected = list.count { it.connected }
        Text(
            "共 ${list.size} 台 · 已连接 $connected 台 · 长按设备设别名",
            style = MaterialTheme.typography.bodySmall
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(list, key = { it.address }) { d ->
                DeviceCard(
                    d = d,
                    onClick = { /* 核心：连接 / 断开 */ },
                    onLongClick = { renaming = d.address }
                )
            }
        }
    }

    renaming?.let { addr ->
        val d = devices[addr]
        if (d != null) {
            var text by remember(addr) { mutableStateOf(d.nickname ?: "") }
            AlertDialog(
                onDismissRequest = { renaming = null },
                title = { Text("设置别名") },
                text = {
                    Column {
                        Text("MAC: $addr", style = MaterialTheme.typography.bodySmall)
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            singleLine = true,
                            label = { Text("如：1号机") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val nick = text.trim()
                        if (nick.isEmpty()) {
                            prefs.edit().remove("nick_$addr").apply()
                        } else {
                            prefs.edit().putString("nick_$addr", nick).apply()
                        }
                        devices[addr]?.let { devices[addr] = it.copy(nickname = nick.ifEmpty { null }) }
                        renaming = null
                    }) { Text("保存") }
                },
                dismissButton = {
                    TextButton(onClick = { renaming = null }) { Text("取消") }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeviceCard(
    d: DeviceUi,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val display = d.nickname ?: d.name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(display, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (d.connected) "已连接" else d.state,
                    color = if (d.connected) Color(0xFF2E7D32) else Color.Gray
                )
            }
            Text(d.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                if (d.connected) d.status else "—",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
