package mabersold

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mabersold.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureTemplating()
    configureRouting()

    //
//    American League
//            vteAL East	W	L	Pct.	GB	Home	Road
//    (2) New York Yankees	99	63	0.611	—	57–24	42–39
//    (4) Toronto Blue Jays	92	70	0.568	7	47–34	45–36
//    (6) Tampa Bay Rays	86	76	0.531	13	51–30	35–46
//    Baltimore Orioles	83	79	0.512	16	45–36	38–43
//    Boston Red Sox	78	84	0.481	21	43–38	35–46
//    vteAL Central	W	L	Pct.	GB	Home	Road
//    (3) Cleveland Guardians	92	70	0.568	—	46–35	46–35
//    Chicago White Sox	81	81	0.500	11	37–44	44–37
//    Minnesota Twins	78	84	0.481	14	46–35	32–49
//    Detroit Tigers	66	96	0.407	26	36–46	30–50
//    Kansas City Royals	65	97	0.401	27	39–42	26–55
//    vteAL West	W	L	Pct.	GB	Home	Road
//    (1) Houston Astros	106	56	0.654	—	55–26	51–30
//    (5) Seattle Mariners	90	72	0.556	16	46–35	44–37
//    Los Angeles Angels	73	89	0.451	33	40–41	33–48
//    Texas Rangers	68	94	0.420	38	34–47	34–47
//    Oakland Athletics	60	102	0.370	46	29–51	31–51
//
//    National League
//            vteNL East	W	L	Pct.	GB	Home	Road
//    (2) Atlanta Braves	101	61	0.623	—	55–26	46–35
//    (4) New York Mets	101	61	0.623	—	54–27	47–34
//    (6) Philadelphia Phillies	87	75	0.537	14	47–34	40–41
//    Miami Marlins	69	93	0.426	32	34–47	35–46
//    Washington Nationals	55	107	0.340	46	26–55	29–52
//    vteNL Central	W	L	Pct.	GB	Home	Road
//    (3) St. Louis Cardinals	93	69	0.574	—	53–28	40–41
//    Milwaukee Brewers	86	76	0.531	7	46–35	40–41
//    Chicago Cubs	74	88	0.457	19	37–44	37–44
//    Pittsburgh Pirates	62	100	0.383	31	34–47	28–53
//    Cincinnati Reds	62	100	0.383	31	33–48	29–52
//    vteNL West	W	L	Pct.	GB	Home	Road
//    (1) Los Angeles Dodgers	111	51	0.685	—	57–24	54–27
//    (5) San Diego Padres	89	73	0.549	22	44–37	45–36
//    San Francisco Giants	81	81	0.500	30	44–37	37–44
//    Arizona Diamondbacks	74	88	0.457	37	40–41	34–47
//    Colorado Rockies	68	94	0.420	43	41–40	27–54
}
