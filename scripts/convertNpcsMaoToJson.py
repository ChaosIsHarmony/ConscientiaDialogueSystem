import json
import os
from typing import List, Tuple



def check_for_accuracy(json_str: str) -> None:
    '''
    Pretty print the JSON string to confirm proper formatting.
    Will raise exceptions if it cannot parse the string due to incorrect formatting.
    '''
    json_obj = json.loads(json_str)
    parsed_json_str = json.dumps(json_obj, indent=2)
    print(parsed_json_str)



def clean_line(line: str) -> str:
    # strip whitespace
    cleaned_line = line[line.find('\t')+1:line.find('\n')]
    # strip parentheses
    start = 0
    end = len(cleaned_line)
    if '(' in cleaned_line:
        start = cleaned_line.find('(')+1
    if ')' in cleaned_line:
        end = cleaned_line.find(')')
    cleaned_line = cleaned_line[start:end]
    # strip comma
    end = len(cleaned_line)
    if ',' in cleaned_line:
        end = cleaned_line.find(',')
    cleaned_line = cleaned_line[:end]

    return cleaned_line



def parse_weakeness(weaknesses: str) -> str:
    start = weaknesses.find('*')+1
    parsed_weaknesses = "["
    while weaknesses[start] != '*':
        parsed_weaknesses += weaknesses[start:weaknesses.find(',', start)] + ","
        start = weaknesses.find(',', start)+1
    parsed_weaknesses = parsed_weaknesses[:-1] + "]"

    return parsed_weaknesses



def parse_sections(contents: List[str]) -> List[List[str]]:
    sections = []
    ind = 0
    while ind < len(contents):
        if '[' in contents[ind]:
            section_name = contents[ind][contents[ind].find('[')+2:contents[ind].find(']')]
            new_section = [section_name]
            ind += 1
            while (section_name+'/') not in contents[ind]:
                if contents[ind] != '\t\n':
                    new_section.append(contents[ind])
                ind += 1
            sections.append(new_section)
        ind += 1

    return sections



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    sections = parse_sections(contents)

    npcsJson = "{"
    for section in sections:
        # name
        name = section[0]
        npcsJson += "\"" + name + "\": {"

        # img
        img_start = section[1].find(',', section[1].find(',')+1)+1
        img_end = section[1].find('}')-1
        img = section[1][img_start:img_end]
        npcsJson += "\"img\": \"" + img + "\", "

        # weaknesses
        weaknesses = parse_weakeness(section[2])
        npcsJson += "\"weaknesses\": " + weaknesses + ", "

        # current dialogue addresses by location
        addresses = "{"
        for i in range(3, len(section)):
            cleaned_line = clean_line(section[i])
            partition = cleaned_line.find(':')
            location = cleaned_line[:partition]
            address = cleaned_line[partition+1:]
            addresses += "\"" + location + "\": \"" + address +"\", "

        addresses = addresses[:-2] + "}"
        npcsJson += "\"addresses\": " + addresses + "}, "

    # trim trailing comma
    npcsJson = npcsJson[:-2] +"}"
    # check for accuracy
    #  check_for_accuracy(npcsJson)

    # dump to file
    jsonFilepath = filepath[:filepath.find('.')] + ".json"
    with open(jsonFilepath, 'w') as f:
        json.dump(npcsJson, f)




if __name__ == "__main__":
    filepath = os.getcwd() + "/resources/TextFiles/Templates/NPCs.mao"
    convert_file(filepath)
