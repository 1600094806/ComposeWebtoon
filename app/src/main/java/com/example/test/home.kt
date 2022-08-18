package com.example.test

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.test.model.SpecialWebtoonItem
import com.example.test.ui.theme.TestTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Column {
        HomeTabBar(onTabSelected = {
            coroutineScope.launch {
                pagerState.scrollToPage(it)
            }
        }, pagerState)
        HomePager(pagerState)
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HomeTabBar(
    onTabSelected: (index: Int) -> Unit,
    pagerState: PagerState
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            HomeTabIndicator(tabPositions, pagerState)
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 80.dp)
    ) {
        HomeTab(
            title = stringResource(R.string.home_recommend),
            onClick = { onTabSelected(0) },
            pagerState = pagerState,
            thisTabPage = 0
        )
        HomeTab(
            title = stringResource(R.string.home_special),
            onClick = { onTabSelected(1) },
            pagerState = pagerState,
            thisTabPage = 1
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HomeTab(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    thisTabPage: Int
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .zIndex(2f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = if (pagerState.currentPage == thisTabPage) Color.Black else Color.White
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HomeTabIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState
) {
    val transition = updateTransition(
        pagerState,
        label = "Tab indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Indicator left"
    ) { page ->
        tabPositions[page.currentPage].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Indicator right"
    ) { page ->
        tabPositions[page.currentPage].right
    }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .zIndex(1f)
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePager(pagerState: PagerState) {
    HorizontalPager(count = 2, state = pagerState) { page ->
        when (page) {
            0 -> RecommendTab()
            else -> SpecialTab()
        }
    }
}

@Composable
fun RecommendTab() {
    Text(text = "추천탭")
}

val specialWebtoonItem = listOf(
    SpecialWebtoonItem("1", "1", "1"),
    SpecialWebtoonItem("2", "2", "2"),
    SpecialWebtoonItem("3", "3", "3"),
    SpecialWebtoonItem("4", "4", "4"),
)

@Composable
fun SpecialTab() {
    LazyColumn(
        modifier = Modifier
            .background(Color.Black)
    ) {
        items(specialWebtoonItem.size) {
            SpecialItem(specialWebtoonItem[it])
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SpecialItem(item: SpecialWebtoonItem) {
    val context = LocalContext.current
    val imgLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val mPainter = rememberAsyncImagePainter(R.drawable.fight, imgLoader)


    ConstraintLayout(modifier = Modifier.height(600.dp)) {
        val (frontImage, backgroundImage, chips) = createRefs()
        Image(
            painter = mPainter,
            contentDescription = "Webtoon",
            modifier = Modifier
                .zIndex(2.0f)
                .fillMaxWidth()
                .constrainAs(frontImage) {
                    bottom.linkTo(backgroundImage.bottom)
                },
            contentScale = ContentScale.Crop
        )
        Image(
            painterResource(id = R.drawable.back),
            contentDescription = "background",
            modifier = Modifier
                .constrainAs(backgroundImage) {
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .height(600.dp),
            contentScale = ContentScale.Crop
        )
        ChipButtons(modifier = Modifier.constrainAs(chips) {
            bottom.linkTo(parent.bottom)
        }.padding(bottom = 16.dp, start = 4.dp)
            .zIndex(3.0f), data = chipString)
    }


}

val chipString = listOf("123", "3452345", "12312321", "124123123","123123123","123123123")

@Composable
fun ChipButtons(modifier: Modifier, data: List<String>) {
    LazyRow(modifier = modifier) {
        items (data.size) {
            Text(
                text = data[it], modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .padding(vertical = 4.dp, horizontal = 8.dp)

                   ,
                color = Color.White

            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Composable
fun SpecialBackground() {

}


@Preview
@Composable
fun prevHome() {
    TestTheme {
        Home()
    }
}

@Preview
@Composable
fun SpecialBackgroundPreview() {
    TestTheme {
        SpecialBackground()
    }
}