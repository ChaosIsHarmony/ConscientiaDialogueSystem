import json
import os
from typing import Dict, List, Tuple



def check_for_accuracy(json_str: str) -> None:
    '''
    Pretty print the JSON string to confirm proper formatting.
    Will raise exceptions if it cannot parse the string due to incorrect formatting.
    '''
    json_obj = json.loads(json_str)
    parsed_json_str = json.dumps(json_obj, indent=2)
    print(parsed_json_str)
    print()



def parse_location(area: List[str]) -> str:
    areaStr = "{"

    ind = 0
    while ind < len(area):
        if '/' in area[ind]:
            areaName = area[ind][area[ind].find('/')+1:area[ind].find(']')]
            areaStr += "\"" + areaName +"\": ["
            ind += 1
            npcListRaw = area[ind]
            npcInd = npcListRaw.find('|')+1
            while npcListRaw[npcInd] != '|':
                areaStr += npcListRaw[npcInd:npcListRaw.find(',', npcInd)] + ", "
                npcInd = npcListRaw.find(',', npcInd)+1
            areaStr = areaStr[:-2] + "], "
            ind += 1
        ind += 1

    areaStr = areaStr[:-2] + "}, "

    return areaStr



def parse_areas(region: List[str]) -> str:
    regionStr = "{"

    areaDict = parse_blocks(region)

    for area in areaDict.keys():
        regionStr += "\"" + area + "\": " + parse_location(areaDict[area])


    regionStr = regionStr[:-2] + "}, "

    return regionStr


def parse_blocks(contents: List[str]) -> Dict[str, List[str]]:
    blockDict = {}

    ind = 0
    while ind < len(contents):
        if '/' in contents[ind]:
            blockName = contents[ind][contents[ind].find('/')+1:contents[ind].find(']')]
            blockList = []
            ind += 1
            while ('['+blockName+'/]') not in contents[ind]:
                #  print(contents[ind])
                blockList.append(contents[ind])
                ind += 1
            blockDict[blockName] = blockList
        ind += 1

    return blockDict



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    regionDict = parse_blocks(contents)
    jsonStr = "{"
    for region in regionDict.keys():
        jsonStr += "\"" + region + "\": " + parse_areas(regionDict[region])

    jsonStr = jsonStr[:-2] + "}"
    # check for accuracy
    #  check_for_accuracy(jsonStr)

    jsonFilepath = filepath[:filepath.find('n.')+1] + ".json"
    print(jsonFilepath)
    with open(jsonFilepath, 'w') as f:
       f.write(jsonStr)


if __name__ == "__main__":
    filepath = os.getcwd() + "/../resources/TextFiles/Structural/NPCListByLocation.mao"
    convert_file(filepath)
