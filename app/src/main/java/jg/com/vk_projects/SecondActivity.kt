package jg.com.vk_projects

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import jg.com.vk_projects.ui.theme.VK_ProjectsTheme

class SecondActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentExtra: Intent = intent

        setContent {
            VK_ProjectsTheme {
                MainContent2(intentExtra)
            }
        }

    }
}

@Composable
fun MainContent2(intent: Intent) {
    val mContext = LocalContext.current
    val mContextActivity = LocalContext.current as? Activity


    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = {
                    mContextActivity?.onBackPressed()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                }
                Text("${intent.getStringExtra("name")}", fontSize = 22.sp, color = Color.White, modifier = Modifier.padding(start = 20.dp))
            }
        },
        content = { MyContent2(intent) }
    )
}

@Composable
fun MyContent2(intent: Intent){
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        val link: String = intent.getStringExtra("service_url").toString()
        append(link)
        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                fontSize = 18.sp,
                textDecoration = TextDecoration.Underline,
            ), start = 0, end = link.length

        )

        addStringAnnotation(
            tag = "URL",
            annotation = intent.getStringExtra("service_url").toString(),
            start = 0,
            end = link.length
        )
    }

    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = rememberImagePainter(data = intent.getStringExtra("icon_url")), contentDescription = "${intent.getStringExtra("id")}",
            modifier = Modifier
                .padding(top = 30.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(25)))
        Text("${intent.getStringExtra("name")}", fontSize = 30.sp, fontWeight = Bold, textAlign = TextAlign.Left, modifier = Modifier.padding(top = 15.dp))
        Text("${intent.getStringExtra("description")}", fontSize = 20.sp, modifier = Modifier.padding(all = 15.dp))
        ClickableText(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = annotatedLinkString,
            style = TextStyle(
                textAlign = TextAlign.Start, fontWeight = Bold, fontSize = 20.sp),
            onClick = {
                annotatedLinkString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            },
        )
    }

}