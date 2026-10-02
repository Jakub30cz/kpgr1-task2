# KPGR1 – úkol 2: drátěný model 3D těles, transformace, kamera a projekce

Interaktivní aplikace (Java 25, čisté Swing, bez externích knihoven), která vykresluje
3D tělesa jako drátěné modely. Veškerá geometrie se zobrazuje vlastním potrubím
(model, pohled, projekce, ořez, viewport) a čáry se rasterizují vlastním Bresenhamovým
algoritmem do `BufferedImage`.

## Zadání ve zkratce

- Zobrazit sadu 3D těles (hranové modely) ze tří skupin, všechna najednou ve scéně.
- Implementovat transformační matice (posun, rotace, měřítko), pohledovou matici
  a perspektivní i ortogonální projekci.
- Volná kamera řízená myší a klávesnicí, výběr objektu myší a jeho transformace
  (lokální i globální).
- Kubiky (Bézier, Ferguson, Coons) v maticovém tvaru, prostorové křivky a bikubická plocha.
- Animace vybraných těles, zobrazení souřadnicových os a gizma aktivního objektu.

## Sestavení a spuštění

```
./run.sh        # přeloží a spustí aplikaci
make test       # přeloží a spustí JUnit 5 testy (./run-tests.sh)
```

Vyžaduje JDK 25. `make run` je zkratka za `./run.sh`.

## Tělesa

Ve scéně je 12 objektů rozmístěných v mřížce v rovině XY (osa Z míří nahoru).

| Skupina | Tělesa |
|---|---|
| 1 | Čtyřstěn (Tetrahedron), jehlan se čtvercovou podstavou (Pyramid), osmistěn (Octahedron) |
| 2 | Válec (Cylinder), kužel (Cone), torus (Torus) |
| 3 | Bézierova, Fergusonova a Coonsova kubika, Vivianiho křivka, kuželová spirála, bikubická plocha (bonus) |

Krychle, šipka a trojúhelník (tělesa ze seznamu) záměrně nejsou použity – každá skupina
má tři a více jiných těles.

Kubiky jsou samostatné objekty s vlastní modelovou maticí (počáteční hodnota = počáteční
model osmistěnu); jejich koncové body leží ve vrcholech osmistěnu. Přesnost křivek a plochy
(2 až 100 segmentů) se mění posuvníkem.

## Ovládání

| Vstup | Akce |
|---|---|
| LMB drag | kamera: yaw/pitch (otáčení pohledu) |
| LMB click (posun < 3 px) | výběr nejbližšího objektu |
| W / S, A / D | kamera dopředu/dozadu, vlevo/vpravo |
| Shift / Ctrl | kamera nahoru / dolů |
| Q / E | náklon kamery (roll) vlevo / vpravo |
| Tab | výběr dalšího objektu |
| T / R / F | režim: posun (translate) / rotace (rotate) / měřítko (scale) |
| X / Y / Z | osa transformace |
| Arrow Up/Right, Down/Left | provést krok + / − |
| L | přepnutí lokální / globální |
| P | přepnutí perspektivní / ortogonální projekce |
| Space | zapnutí/vypnutí animace |
| Slider | přesnost křivek a ploch, 2..100 segmentů |

Kroky transformací: posun 0,1, rotace 5°, měřítko ×1,05 (při kroku − dělení).
Vybraný objekt je žlutý.

## Potrubí (pipeline)

- Používají se **řádkové vektory**: `p' = p · M`, součin `a.mul(b)` znamená `a · b`.
  Celková transformace bodu je `p · Model · View · Proj` (M·V·P).
- Matice: `Mat4RotZ(a)` převádí (1,0,0) na (cos a, sin a, 0), pravotočivá soustava.
  `Mat4PerspRH`, `Mat4OrthoRH` a `Mat4ViewRH` dávají ořezový prostor
  `-w ≤ x ≤ w`, `-w ≤ y ≤ w`, `0 ≤ z ≤ w`.
- **Striktní ořez podle W („fast W clip")**: hrana se zahodí celá, pokud některý její
  koncový bod má `w ≤ 0` nebo porušuje nerovnosti ořezového prostoru. Hrany se nezkracují.
- Po ořezu následuje dehomogenizace a viewport:
  `xs = round((x/w + 1)·(W−1)/2)`, `ys = round((1 − y/w)·(H−1)/2)`.
- **Lokální vs. globální transformace**:
  - lokální (vlastní osy a počátek objektu): `model = T · model`,
  - globální (osy světa): `model = model · T`; rotace a měřítko se provádějí kolem
    středu objektu `c`: `model = model · Transl(−c) · T · Transl(c)`.
- Osy světa se kreslí maticí `View·Proj` (zůstávají v počátku), gizmo aktivního objektu
  maticí `Transl(střed)·View·Proj`.
- Výběr myší: nejbližší promítnutá hrana v pixelech (nejvýše 6 px), jinak výběr zůstává.
- Kamera má vlastní ortonormální bázi (pozice, forward, right, up), která se po každé
  rotaci znovu ortonormalizuje.

## Struktura

```
src/transforms/        Mat4, Vec3D, Point3D, matice transformací, Camera, Cubic, Bicubic
src/kpgr2/raster/      Raster, Bresenhamův rasterizér, RasterBufferedImage
src/kpgr2/model/       Solid (vrcholy + hrany + barva + modelová matice), Tessellated
src/kpgr2/render/      Pipeline (M·V·P, ořez, viewport), WireRenderer, Picker, ScreenSegment
src/kpgr2/solids/      tělesa, křivky, plocha, osy a gizmo
src/kpgr2/scene/       Scene (sestavení scény, animace), TransformController
src/kpgr2/ui/          ViewPanel (vykreslení a vstup), MainWindow (okno, stavový řádek, posuvník)
src/kpgr2/App.java     vstupní bod
test/                  JUnit 5 testy
```

## Použití AI

Kód vznikl s pomocí Claude Code (Anthropic) a jeho agentů. Postup byl následující:
nejdříve jsem s AI nad zadáním odsouhlasil návrh (klasické potrubí, struktury těles,
ovládání kamery a výběru), z něj vznikl plán rozdělený na malé úlohy a jednotlivé úlohy
implementovali AI agenti metodou TDD (nejdřív selhávající test, poté implementace).
Výsledek procházela kontrola kódu (review) a zjednodušení a pro každou úlohu i celá sada
testů. Rasterizační kód (Bresenham) vychází z mého předchozího úkolu KPGR1 (úkol 1).
AI tedy napsala většinu zdrojového kódu a testů; návrh, rozhodnutí a závěrečné ověření
(spuštění a vyzkoušení aplikace) jsou moje. README bylo rovněž vygenerováno s pomocí AI.
