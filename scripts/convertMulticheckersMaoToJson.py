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



def parse_events(event_str: str) -> str:
    parsed_event_str = ""
    
    if '^' in event_str:
        parsed_event_str += "\"and\": ["
        start_ind = 0
        while start_ind < len(event_str)-1:
            start_ind = event_str.find('^', start_ind) + 1
            end_ind = event_str.find('^', start_ind)
            parsed_event_str += "\"" + event_str[start_ind:end_ind] + "\","
            start_ind = end_ind
        parsed_event_str = parsed_event_str[:-1] + "]"
    elif '$' in event_str:
        parsed_event_str += "\"or\": ["
        start_ind = 0
        while start_ind < len(event_str)-1:
            start_ind = event_str.find('$', start_ind) + 1
            end_ind = event_str.find('$', start_ind)
            parsed_event_str += "\"" + event_str[start_ind:end_ind] + "\","
            start_ind = end_ind
        parsed_event_str = parsed_event_str[:-1] + "]"        
    else:
        parsed_event_str += "\"simple\": [\"" + event_str + "\"]"
    
    return parsed_event_str



def parse_case(block: List[str], start_ind: int, rank: int) -> Tuple[str, int]:
    conditionStr = "\"" + str(rank) + "\": {"
    ind = start_ind
    
    event_start = block[ind+1].find('|')+1
    event_end = block[ind+1].find(':')
    events = parse_events(block[ind+1][event_start:event_end]) + ", "
    dest_add_start = event_end+1
    dest_add_end = block[ind+1].find(',')
    dest_add = "\"dest_add\": \"" + block[ind+1][dest_add_start:dest_add_end] + "\", "
    description = "\"description\": \"" + block[ind][block[ind].find("//")+2:].strip() + "\""
    
    conditionStr += events + dest_add + description + "}, "
    
    ind += 2
    
    return conditionStr, ind


def jsonifyBlocks(blocks: Dict[str, List[str]]) -> str:
    blocksJson = ""
    for address in blocks.keys():
        #print(address)
        blockJson = "\"" + address + "\": {"
        ind = 0
        rank = 0
        while ind < len(blocks[address]):
            case, ind = parse_case(blocks[address], ind, rank)
            blockJson += case
            rank += 1
            
        blockJson = blockJson[:-2] + "}, "
        blocksJson += blockJson
    
    return blocksJson



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
        blocks = parse_blocks(checkerStr[name])
        if len(blocks) > 1:
            jsonStr += jsonifyBlocks(blocks)
        
    jsonStr = jsonStr[:-2] + "}"
        
    print(jsonStr[:50])
    
    # check for accuracy
    check_for_accuracy(jsonStr)

    # dump to file
    jsonFilepath = os.getcwd() + "/../resources/TextFiles/Multicheckers/Multichecker.json"
    with open(jsonFilepath, 'w') as f:
        f.write(jsonStr)



if __name__ == "__main__":
    filepaths = glob.glob(os.getcwd() + "/../resources/TextFiles/Multicheckers/*.mao")
    
    checkerStr = {}
    ind = 0
    for filepath in filepaths:
        with open(filepath) as f:
            checkerStr[names[ind]] = f.readlines()
            ind += 1
    
    convert_to_json(checkerStr)
