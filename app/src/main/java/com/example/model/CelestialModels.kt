package com.example.model

import androidx.compose.ui.graphics.Color

data class CelestialStat(
    val label: String,
    val value: String,
    val unit: String
)

data class CelestialBody(
    val id: String,
    val name: String,
    val en: String,
    val code: String,
    val category: String,
    val tag: String,
    val title: String,
    val description: String,
    val stats: List<CelestialStat>,
    val note: String,
    val source: String,
    val sourceLabel: String,
    val scale: String,
    val primaryColor: Color
)

data class SolarBody(
    val id: String,
    val name: String,
    val en: String,
    val radius: Float,
    val orbit: Float,
    val period: Float,
    val phase: Float,
    val tilt: Float,
    val spin: Float,
    val color: Color,
    val kind: String,
    val detail: String = ""
)

data class KidChapter(
    val id: String,
    val number: String,
    val name: String,
    val eyebrow: String,
    val title: String,
    val line: String,
    val speech: String,
    val detail: String,
    val clue: String,
    val response: String,
    val altitude: String,
    val progress: Float
)

object CelestialRepository {
    val worlds = listOf(
        CelestialBody(
            id = "milkyway",
            name = "银河系",
            en = "Milky Way",
            code = "GAL / 001",
            category = "棒旋星系",
            tag = "我们的宇宙家园",
            title = "每一束光，都有一段旅程。",
            description = "穿过星际尘埃，沿着旋臂的光芒，重新发现我们在宇宙中的坐标。",
            stats = listOf(
                CelestialStat("直径", "100,000+", "光年"),
                CelestialStat("恒星数量", "1,000–4,000", "亿颗"),
                CelestialStat("结构", "SBbc", "棒旋星系")
            ),
            note = "太阳系位于猎户座支臂，距离银心约 26,000 光年。中心存在超大质量黑洞人马座 A*。",
            source = "https://science.nasa.gov/universe/galaxies/",
            sourceLabel = "NASA · Galaxies",
            scale = "25,000 ly",
            primaryColor = Color(0xFF38BDF8)
        ),
        CelestialBody(
            id = "solar",
            name = "太阳系",
            en = "Solar System",
            code = "SOL / 006",
            category = "恒星与行星系统",
            tag = "一颗恒星，八个世界",
            title = "围绕一束光，周而复始。",
            description = "从炽热的太阳，到遥远的海王星。跟随八大行星的轨道，探索我们的星际邻里。",
            stats = listOf(
                CelestialStat("中央恒星", "太阳", "Sun"),
                CelestialStat("行星数量", "8", "颗"),
                CelestialStat("所在位置", "猎户座支臂", "银河系")
            ),
            note = "太阳与八大行星按距离顺序排列。土星保留倾斜星环，地球旁带有月球。公转与自转速率经过观测演示优化。",
            source = "https://science.nasa.gov/solar-system/planets/",
            sourceLabel = "NASA · Solar System Planets",
            scale = "轨道示意 · 非等比例",
            primaryColor = Color(0xFFFBBF24)
        ),
        CelestialBody(
            id = "andromeda",
            name = "仙女座星系",
            en = "Andromeda",
            code = "M31 / 002",
            category = "涡旋星系",
            tag = "来自 250 万年前的光",
            title = "望向邻居，也是望向过去。",
            description = "一座比银河系更辽阔的星辰之岛。此刻抵达的光，在人类出现前便已启程。",
            stats = listOf(
                CelestialStat("距离地球", "250", "万光年"),
                CelestialStat("天体编号", "M31", "NGC 224"),
                CelestialStat("所属星群", "本星系群", "Local Group")
            ),
            note = "明亮的中央核球、绵延的尘埃带，构成仙女座独特的漩涡结构。约在 40 亿年后它将与银河系发生壮丽碰撞。",
            source = "https://science.nasa.gov/photojournal/andromeda/",
            sourceLabel = "NASA · Andromeda",
            scale = "50,000 ly",
            primaryColor = Color(0xFFA855F7)
        ),
        CelestialBody(
            id = "blackhole",
            name = "卡冈图雅",
            en = "Gargantua",
            code = "BH / 003",
            category = "黑洞 · 电影灵感",
            tag = "越过光的边界",
            title = "在这里，光也改变方向。",
            description = "灵感来自《星际穿越》。炽热的吸积盘穿过扭曲的时空，环抱一片绝对的黑暗。",
            stats = listOf(
                CelestialStat("核心结构", "事件视界", "Event horizon"),
                CelestialStat("盘面物质", "高温等离子体", "Accretion disk"),
                CelestialStat("视觉现象", "引力透镜", "Gravitational lensing")
            ),
            note = "黑洞为电影灵感的可视化模型；光线弯曲采用强引力场投影近似，呈现事件视界周围的光子球环与弯折吸积盘。",
            source = "https://svs.gsfc.nasa.gov/13326/",
            sourceLabel = "NASA · Black Hole Visualization",
            scale = "5 rs",
            primaryColor = Color(0xFFF97316)
        ),
        CelestialBody(
            id = "earth",
            name = "地球",
            en = "Earth",
            code = "SOL / 004",
            category = "类地行星",
            tag = "所有故事开始的地方",
            title = "浩瀚之中，这一抹蓝。",
            description = "掠过海洋、山脉与流动的云层。在昼夜交界处，寻找属于我们的灯火。",
            stats = listOf(
                CelestialStat("平均半径", "6,371", "公里"),
                CelestialStat("距太阳", "1.0", "天文单位"),
                CelestialStat("天然卫星", "1", "月球")
            ),
            note = "高精度行星地表投影、独立云层演化、昼夜晨昏交界线与大气瑞利散射辉光。地月距离经过视觉优化展示。",
            source = "https://science.nasa.gov/earth/facts/",
            sourceLabel = "NASA · Earth Facts",
            scale = "5,000 km",
            primaryColor = Color(0xFF06B6D4)
        ),
        CelestialBody(
            id = "moon",
            name = "月球",
            en = "Moon",
            code = "SOL / 005",
            category = "地球的天然卫星",
            tag = "寂静，刻在每一座陨石坑里",
            title = "离我们最近的，另一个世界。",
            description = "沿着明暗交界线，看陨石坑投下长长的影子。月海记录着数十亿年的寂静。",
            stats = listOf(
                CelestialStat("平均半径", "1,737.4", "公里"),
                CelestialStat("平均距离", "384,400", "千米"),
                CelestialStat("公转周期", "27.3", "天")
            ),
            note = "月面撞击坑群、风暴洋与静海等主要月海地质特征。月球处于潮汐锁定状态，总以同一面朝向地球。",
            source = "https://science.nasa.gov/moon/facts/",
            sourceLabel = "NASA · Moon Facts",
            scale = "1,000 km",
            primaryColor = Color(0xFF94A3B8)
        )
    )

    val solarBodies = listOf(
        SolarBody("sun", "太阳", "Sun", radius = 1.8f, orbit = 0f, period = 1f, phase = 0f, tilt = 7.25f, spin = 0.06f, color = Color(0xFFFFD166), kind = "恒星", detail = "太阳系的核心，集中了全系统 99.86% 的质量，通过核聚变照耀各大行星。"),
        SolarBody("mercury", "水星", "Mercury", radius = 0.35f, orbit = 40f, period = 0.241f, phase = 2.35f, tilt = 0.03f, spin = 0.025f, color = Color(0xFFBDB4A5), kind = "类地行星", detail = "离太阳最近的行星，表面昼夜温差高达 600 摄氏度。"),
        SolarBody("venus", "金星", "Venus", radius = 0.55f, orbit = 60f, period = 0.615f, phase = 4.45f, tilt = 177.4f, spin = 0.012f, color = Color(0xFFEBC38B), kind = "类地行星", detail = "浓密二氧化碳大气层带来强烈的温室效应，表面温度高达 460℃。"),
        SolarBody("earth", "地球", "Earth", radius = 0.58f, orbit = 85f, period = 1.0f, phase = 5.95f, tilt = 23.44f, spin = 0.45f, color = Color(0xFF60A5FA), kind = "类地行星", detail = "孕育生命的蔚蓝家园，拥有液态海洋与富氧大气。"),
        SolarBody("mars", "火星", "Mars", radius = 0.42f, orbit = 115f, period = 1.881f, phase = 3.4f, tilt = 25.19f, spin = 0.43f, color = Color(0xFFF87171), kind = "类地行星", detail = "红色行星，表面遍布氧化铁沙尘，拥有太阳系最高的奥林帕斯火山。"),
        SolarBody("jupiter", "木星", "Jupiter", radius = 1.25f, orbit = 160f, period = 11.86f, phase = 0.25f, tilt = 3.13f, spin = 1.05f, color = Color(0xFFF59E0B), kind = "气态巨行星", detail = "太阳系最大的行星，著名的大红斑风暴已持续旋转了数百年。"),
        SolarBody("saturn", "土星", "Saturn", radius = 1.05f, orbit = 210f, period = 29.46f, phase = 2.85f, tilt = 26.73f, spin = 0.98f, color = Color(0xFFFDE68A), kind = "气态巨行星", detail = "拥有太阳系最为壮丽的冰晶光环系统，平均密度比水还小。"),
        SolarBody("uranus", "天王星", "Uranus", radius = 0.75f, orbit = 260f, period = 84.01f, phase = 4.65f, tilt = 97.77f, spin = 0.61f, color = Color(0xFF67E8F9), kind = "冰巨行星", detail = "以几乎“躺着”的方式绕太阳公转，大气中富含甲烷使其呈现青色。"),
        SolarBody("neptune", "海王星", "Neptune", radius = 0.72f, orbit = 310f, period = 164.8f, phase = 5.9f, tilt = 28.32f, spin = 0.65f, color = Color(0xFF3B82F6), kind = "冰巨行星", detail = "狂暴的深蓝世界，拥有太阳系最快的超音速风暴。")
    )

    val kidChapters = listOf(
        KidChapter(
            id = "launch",
            number = "01",
            name = "发射台",
            eyebrow = "坐好啦，我们准备出发",
            title = "坐进火箭，看看窗外。",
            line = "我家就在前面。准备好一起升空了吗？",
            speech = "嗨，小小宇航员！我们已经坐进火箭啦。看看窗外，我们的小房子就在前面。坐稳喽，准备出发！",
            detail = "从火箭舷窗向外看，发射塔架与地面基地静静伫立，倒数计时即将归零。",
            clue = "和地面挥挥手",
            response = "再见，地球上的小房子！我们要去深空探险啦！",
            altitude = "海拔 12 米 · 塔架就绪",
            progress = 0.05f
        ),
        KidChapter(
            id = "above-city",
            number = "02",
            name = "城市上空",
            eyebrow = "我们正在慢慢升高",
            title = "房子，慢慢变小啦。",
            line = "看，道路和房子都在我们的脚下！",
            speech = "火箭慢慢升高了。房子变小啦，道路也变细啦。我们正在飞过城市的上空！",
            detail = "地面上的高楼大厦和公路车流变成了一幅微缩积木画卷。",
            clue = "寻找小车和马路",
            response = "哇！小汽车像小蚂蚁一样在马路上爬呢！",
            altitude = "高度 3,200 米 · 俯瞰城市",
            progress = 0.22f
        ),
        KidChapter(
            id = "clouds",
            number = "03",
            name = "穿过云层",
            eyebrow = "窗外，白白软软的云",
            title = "我们飞到云上面啦！",
            line = "云慢慢来到下面，天空越来越深。",
            speech = "看，白白的云从窗外经过。像棉花糖一样！我们飞到云的上面啦，天空的颜色越来越深了。",
            detail = "突破平流层与对流层，下方是洁白的云海，上方是渐深的深邃天幕。",
            clue = "触摸棉花糖白云",
            response = "咻的一下！我们钻过厚厚的云层啦！",
            altitude = "高度 12,000 米 · 穿越对流层",
            progress = 0.42f
        ),
        KidChapter(
            id = "horizon",
            number = "04",
            name = "弯弯地球",
            eyebrow = "远处，出现了一条弧线",
            title = "地平线，变弯啦。",
            line = "地球很大很大，它的边缘是弯弯的。",
            speech = "看远处！地平线不再是笔直的，它变成了一条温柔的蓝色弧线。地球真的是圆圆的！",
            detail = "卡门线之上，地球美丽的弧度尽收眼底，薄薄的大气层如同一层光晕护卫着家园。",
            clue = "看地球的蓝色外衣",
            response = "那就是大气层！它像被子一样保护着所有小生命呢。",
            altitude = "高度 110 公里 · 跨越卡门线",
            progress = 0.65f
        ),
        KidChapter(
            id = "space",
            number = "05",
            name = "进入太空",
            eyebrow = "天空，变成深深的颜色",
            title = "窗外，是太空了。",
            line = "蓝色的地球就在下面，周围越来越黑，星星亮起来了。",
            speech = "天空完全变成漆黑的深空了！没有了空气的散射，无数闪烁的星星在向我们眨眼睛。",
            detail = "失重状态降临，漆黑的宇宙背景中，恒星不再闪烁，纯粹而明澈。",
            clue = "数数天上的星星",
            response = "一、二、三……天上的星星多得数不清，好神奇啊！",
            altitude = "高度 380 公里 · 轨道飞行",
            progress = 0.85f
        ),
        KidChapter(
            id = "look-back",
            number = "06",
            name = "回望地球",
            eyebrow = "飞得好远，也记得我们的家",
            title = "原来，这就是我们的家。",
            line = "房子、城市和我们，都在这颗地球上。",
            speech = "回过头看看，原来我们生活的整个世界，是这颗在漆黑宇宙中悬浮着的蓝色弹珠。好温暖啊！",
            detail = "在无垠宇宙中回望母星，这是一颗满载生命的奇迹之星。",
            clue = "给地球一个拥抱",
            response = "亲爱的地球家园，我们永远爱您！",
            altitude = "高度 1,200 公里 · 深空回望",
            progress = 1.0f
        )
    )
}
