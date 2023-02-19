package jg.com.vk_projects

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import jg.com.vk_projects.data_class.Item
import jg.com.vk_projects.data_class.VkService
import jg.com.vk_projects.ui.theme.Blue700
import jg.com.vk_projects.ui.theme.VK_ProjectsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class MainActivity : ComponentActivity() {
    private val wifiSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (isWifiEnabled(this)) {
                recreate()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wifiManager = (this as Activity).getSystemService(Context.WIFI_SERVICE) as WifiManager
        setContent {
            VK_ProjectsTheme() {
                MyScreen(wifiSettingsLauncher, LocalContext.current as Activity)
            }

        }

    }

    suspend fun getData(): Item? = withContext(Dispatchers.IO) {
        val url = URL("https://mobile-olympiad-trajectory.hb.bizmrg.com/semi-final-data.json")

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 1000
            connection.readTimeout = 1500
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val gson = Gson()
            print(jsonString)
            gson.fromJson(jsonString, Item::class.java)
        } catch (e: SocketTimeoutException) {
            null
        }
    }


    @Composable
    fun LazyRecyclerView(selectedItem: (String) -> (Unit)) {
        val mContext = LocalContext.current
        val data = remember { mutableStateOf<List<VkService>>(emptyList()) }
        LaunchedEffect(Unit) {
            try {
                data.value = getData()?.items ?: throw Exception()
            } catch (e: Exception) {
                data.value = emptyList()
            }

        }
        if (data.value.isNotEmpty()) {
            androidx.compose.foundation.lazy.LazyColumn {
                items(data.value.size) {
                    Row(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = true,
                                    radius = 300.dp,
                                    color = Blue700
                                ),
                                onClick = {
                                    val intent = Intent(mContext, SecondActivity::class.java)
                                    intent.putExtra(
                                        "name",
                                        data.value[Integer.parseInt("$it")].name
                                    )
                                    intent.putExtra(
                                        "description",
                                        data.value[Integer.parseInt("$it")].description
                                    )
                                    intent.putExtra(
                                        "service_url",
                                        data.value[Integer.parseInt("$it")].service_url
                                    )
                                    intent.putExtra(
                                        "icon_url",
                                        data.value[Integer.parseInt("$it")].icon_url.toString()
                                    )
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mContext.startActivity(intent)
                                })
                    ) {
                        val painter =
                            rememberImagePainter(data = data.value[Integer.parseInt("$it")].icon_url)
                        Image(
                            painter = painter,
                            contentDescription = "logo",
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .size(70.dp)
                                .clip(RoundedCornerShape(25))
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        Column {
                            Text(
                                text = data.value[Integer.parseInt("$it")].name,
                                fontSize = 25.sp,
                                modifier = Modifier.padding(top = 18.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(
                    "Если информация долго не появляется, то проверьте Ваше интернет соединение",
                    fontSize = 25.sp,
                    modifier = Modifier.padding(all = 30.dp)
                )
            }
        }
    }

    @Composable
    fun noInternet(wifiSettingsLauncher: ActivityResultLauncher<Intent>) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(
                "Если информация долго не появляется, то проверьте Ваше интернет соединение",
                fontSize = 25.sp,
                modifier = Modifier.padding(all = 30.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(Settings.Panel.ACTION_WIFI)
                    wifiSettingsLauncher.launch(intent)
                }
            ) {
                Text("Открыть настройки Wi-Fi", fontSize = 20.sp)
            }

        }

    }

    @Composable
    fun MyScreen(wifi: ActivityResultLauncher<Intent>, mContext: Activity) {

        if (isWifiEnabled(mContext)) {
            LazyRecyclerView({})
        } else {
            noInternet(wifi)
        }
    }

    fun isWifiEnabled(mContext: Activity): Boolean {
        val wifiManager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }
}

