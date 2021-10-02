import glob
import json
import os
from typing import Dict, List, Tuple

names = ["Enclave", "Jer", "Kabu", "Kavu", "Mind", "Thiuda", "Urugh"]

def check_for_accuracy(json_str: str) -> None:
    '''
    Pretty print the JSON string to confirm proper formatting.
    Will raise exceptions if it cannot parse the string due to incorrect formatting.
    '''
    json_obj = json.loads(json_str)
    parsed_json_str = json.dumps(json_obj, indent=2)
    print(parsed_json_str)



def parse_case(block: List[str], start_ind) -> Tuple[str, int]:
    string = "\"" + str(start_ind) + "\": {}, "
    ind = start_ind + 1
    
    for line in block:
        if "{" in line:
            print(line)
    
    return string, ind


def jsonifyBlocks(blocks: Dict[str, List[str]]) -> str:
    blockJson = "{"
    for address in blocks.keys():
        #print(address)
        blockJson += "\"" + address + "\": {"
        ind = 0
        while ind < len(blocks[address]):
            case, ind = parse_case(blocks[address], ind)
            blockJson += case
            
        blockJson = blockJson[:-2] + "}, "
        
    blockJson = blockJson[:-2] + "}, "
    
    return blockJson



def parse_blocks(fileStrList: List[str]) -> Dict[str, List[str]]:
    blocks = {}
    ind = 0
    while ind < len(fileStrList):
        line = fileStrList[ind]
        #Isolate blocks
        if '{' in line:
            block = []
            address = line[line.find('{')+1:line.find('}')]
            ind += 1
            
            while address not in fileStrList[ind]:
                block.append(fileStrList[ind])
                ind += 1
            blocks[address] = block
        ind += 1
    
    return blocks
    


def convert_to_json(checkerStr: Dict[str, List[str]]) -> None:

    jsonStr = "{"
    for name in names:
        jsonStr += "\"" + name + "\": "
        blocks = parse_blocks(checkerStr[name])
        if len(blocks) > 1:
            jsonStr += jsonifyBlocks(blocks)
        else:
            jsonStr += "{}, "
        
    jsonStr = jsonStr[:-2] + "}"
    
    #print(jsonStr)
    
    # check for accuracy
    #check_for_accuracy(jsonStr)

    
def unreachable():
    while true:
        ind = 0
        while ind < len(checkerStr[name]):
            line = checkerStr[name][ind]
            if '{' in line:
                entry = {}
                address = line[line.find('{')+1:line.find('}')]
                ind += 1
                
                print("\n\nADDRESS: " + address)
                while address not in checkerStr[name][ind]:
                    line = checkerStr[name][ind]
                    if '//' in line:
                        description = line[line.find('//')+2:]
                        while '//' in checkerStr[name][ind+1]:
                            ind += 1
                            line = checkerStr[name][ind]
                            description += line[line.find('//')+2:]
                        entry["description"] = description
                        print("Description: " + entry["description"])
                    elif '|' in line:
                        event = line[line.find('|')+1:line.find(':')]
                        dest_add = line[line.find(':')+1:line.find(',')]
                        if '$' in event:
                            print("OR: " + event + dest_add)
                        elif '^' in event:
                            print("AND: " + event + dest_add)
                        else:
                            print("SIMPLE: " + event + dest_add)
                    elif '{' in line:
                        print("Subline: " + line)
                    else:
                        print("WTF")
                    ind += 1
            ind += 1
    
    jsonStr += "}"
    
    
    # dump to file
    jsonFilepath = os.getcwd() + "/../resources/TextFiles/Multicheckers/Multichecker.json"
    #with open(jsonFilepath, 'w') as f:
        #f.write(jsonStr)



if __name__ == "__main__":
    filepaths = glob.glob(os.getcwd() + "/../resources/TextFiles/Multicheckers/*.mao")
    
    checkerStr = {}
    ind = 0
    for filepath in filepaths:
        with open(filepath) as f:
            checkerStr[names[ind]] = f.readlines()
            ind += 1
    
    convert_to_json(checkerStr)
