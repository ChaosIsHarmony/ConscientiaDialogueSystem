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



def escape_special_chars(text: str) -> str:
    '''
    Replaces special characters with properly formatted JSON versions.
    Special characters include tabs, new lines, quotation marks, etc.
    '''
    inds_of_quotes = []
    inds_of_whitespaces = []
    # find index of special characters
    hasTabs = False
    for i in range(len(text)):
        if text[i] == '\"':
            # add len to compensate for shift due to adding escape slash
            inds_of_quotes.append(i+len(inds_of_quotes))
        elif text[i] == '\t' or text[i] == '\n':
            # add len to compensate for shift due to adding escape slash
            # two for each element in whitespace '\' + '\n' or '\t'
            # also add the first quotation mark if it exists
            inds_of_whitespaces.append(i+(len(inds_of_whitespaces)*2)+len(inds_of_quotes))
    # insert escape slash
    for i in inds_of_quotes:
        text = text[:i] + "\\" + text[i:]
    for i in inds_of_whitespaces:
        if text[i] == '\t':
            text = text[:i] + "\\\\t" + text[i+1:]
        else:
            text = text[:i] + "\\\\n" + text[i+1:]

    return text



def parse_combat_descriptions(contents: List[str]) -> str:
    jsonStr = "{"
    
    ind = 0
    while ind < len(contents):
        line = contents[ind]
        if '*' in line:
            weaponId = line[line.find('*')+1 : line.find(':')]
            ind += 1
            combatDescription = ""
            while '*' not in contents[ind]:
                combatDescription += contents[ind]
                ind += 1
            combatDescription = escape_special_chars(combatDescription)
            jsonStr += "\"" + weaponId + "\": \"" + combatDescription + "\", "
        if '@' in line:
            weaponId = "default"
            ind += 1
            combatDescription = ""
            while '@' not in contents[ind]:
                combatDescription += contents[ind]
                ind += 1
            combatDescription = escape_special_chars(combatDescription)
            jsonStr += "\"" + weaponId + "\": \"" + combatDescription + "\", "
        ind += 1
    
    jsonStr = jsonStr[:-2] + "}, "
    
    return jsonStr



def parse_sections(contents: List[str]) -> Dict[str, List[str]]:
    npcDict = {}
    
    ind = 0
    while ind < len(contents):
        line = contents[ind]
        if "[/" in line:
            id = line[line.find('/')+1 : line.find(']')]
            combatStrs = []
            ind += 1
            while (id+"/]") not in contents[ind]:
                combatStrs.append(contents[ind])
                ind += 1
            npcDict[id] = combatStrs
        ind += 1
    
    return npcDict



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    combatMaoDict = parse_sections(contents)
    
    jsonStr = "{"
    for key in combatMaoDict.keys():
        jsonStr += "\"" + key + "\": " + parse_combat_descriptions(combatMaoDict[key])
    
    jsonStr = jsonStr[:-2] + "}"

    # check for accuracy
    check_for_accuracy(jsonStr)

    jsonFilepath = filepath[:filepath.find('.mao')] + ".json"
    with open(jsonFilepath, 'w') as f:
        f.write(jsonStr)
        #json.dump(jsonStr, f)


if __name__ == "__main__":
    filepath = os.getcwd() + "/../resources/TextFiles/NonDialogueText/CombatDescriptions.mao"
    convert_file(filepath)
