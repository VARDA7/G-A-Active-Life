package com.fitnessapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fitnessapp.R
import kotlinx.coroutines.delay


@Composable

 fun G1(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize().background(Color.White).padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

        Image(painter = painterResource(id = R.drawable.lp1), contentDescription = "")

            Spacer(modifier = Modifier.height(50.dp)) // bu sayade alt alta boşluk bırakabiliyoruz

            Text(
                text = "G&A Active Life, sağlıklı yaşamı ve aktif olmayı teşvik eden bir platformdur. Günlük egzersizler, motivasyon ve sağlıklı yaşam tüyoları burada!", // yazı değişecek
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Button(
            onClick =  { navController.navigate("G3")},
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 42.dp).height(60.dp).width(240.dp) ,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ){
            Text( text = "Hadi Başlayalım ! ", fontSize = 23.sp , color=Color.White)
        }
    }
}