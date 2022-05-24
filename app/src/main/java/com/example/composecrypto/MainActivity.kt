package com.example.composecrypto

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.composecrypto.ui.theme.ComposeCryptoTheme
import java.io.IOException
import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonArray
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*

lateinit var cache: DiskBasedCache

// Set up the network to use HttpURLConnection as the HTTP client.
lateinit var network : BasicNetwork

// Instantiate the RequestQueue with the cache and network. Start the queue.
lateinit var requestQueue : RequestQueue
var tmpName : String = ""
var tmpUsdPrice : String = ""
var tmpImage : String = ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        network = BasicNetwork(HurlStack())

        requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        setContent {
            ComposeCryptoTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CoilImage("$tmpImage")
                }
            }

            Column() {
                Column(modifier = Modifier
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                    val text =  remember { mutableStateOf("") }
                    TextField(value = text.value, onValueChange = {text.value = it}, label = {Text(text="Введите криптовалюту")})
                    getInfo(text.value)
                    
                    Text(text = "$tmpName \n")
                    Text(text = "$tmpUsdPrice USD")
                    CoilImage(image = "$tmpImage")
                }
            }
        }
    }
}

fun getInfo(coin : String){
    val url = "https://api.coingecko.com/api/v3/coins/$coin"
    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            println("Response: %s".format(response.toString()))

            tmpName = response.getString("name")
            tmpUsdPrice = response.getJSONObject("market_data").getJSONObject("current_price").getString("usd")
            tmpImage = response.getJSONObject("image").getString("large")

            println("$tmpName $tmpUsdPrice $tmpImage")
            Log.d("TAG", "$tmpName $tmpUsdPrice $tmpImage")
        },
        { error ->
            println("L $error")
        }
    )
    requestQueue.add(jsonObjectRequest)
}

@Composable
fun CoilImage(image : String){
    Box(
        modifier = Modifier
            .height(150.dp)
            .width(150.dp),
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = image,
            builder = {

            })
        Image(painter = painter, contentDescription = "Logo Image")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeCryptoTheme {
        Greeting("Android")
    }
}