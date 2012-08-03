import time

def openBuildHousing():
    click("1343952152782.png")
    click("1343952403782.png")


switchApp("DroidTowers")
time.sleep(5)
newTowerButton = Pattern("r1whvwr.png").similar(0.83)
wait(newTowerButton)
click(newTowerButton)

randomTowerName = "1343951489279.png"
click(randomTowerName)
click("1343951524913.png")








wait("WELCOMETODRO.png")
openBuildHousing()
click("1343952430889.png")

#closeApp("DroidTowers")
