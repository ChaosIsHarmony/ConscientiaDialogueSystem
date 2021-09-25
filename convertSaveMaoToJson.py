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



def parse_triggered_events(triggeredEvents: List[str]) -> str:
    triggeredEventsJson = "\"triggered_events\": ["
    for event in triggeredEvents:
        if '|' not in event:
            continue

        eventJson = "{"
        event_id = event[event.find('|')+1:event.find(':')]
        desc = event[event.find(',')+2:event.find('\n')]
        eventJson += "\"" + event_id +"\": { \"value\": false, \"description\": \"" + desc +"\"}}, "
        triggeredEventsJson += eventJson

    triggeredEventsJson = triggeredEventsJson[:-2] + "]"

    return triggeredEventsJson



def parse_sections(contents: List[str]) -> List[List[str]]:
    sections = []
    ind = 0
    while ind < len(contents):
        if '{' in contents[ind]:
            new_section = [contents[ind]]
            section_name = contents[ind]
            ind += 1
            while section_name not in contents[ind]:
                new_section.append(contents[ind])
                ind += 1
            sections.append(new_section)
        ind += 1

    return sections



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    sections = parse_sections(contents)

    # parse current location
    currentLocationJson = "\"current_location\": { \"address\": \"\", \"description\": \"" + sections[0][1][sections[0][1].find(',')+2:sections[0][1].find('\n')] + "\"}, "

    # parse current npc
    currentNpcJson = "\"current_npc\": { \"id\": 9999, \"description\": \"Current interlocutor\"}, "

    # parse current mindscape location
    mindscapeCurrentLocationJson = "\"location_before_entering\": { \"address\": \"\", \"description\": \"" + sections[2][1][sections[0][1].find(',')+2:sections[2][1].find('\n')] + "\"}, "

    # parse current mindscape npc
    mindscapeCurrentNpcJson = "\"mindscape_current_npc\": { \"id\": 9999, \"description\": \"Current mindscape interlocutor\"}, "

    # parse volatile acquirables
    volatileAcquirablesJson = "\"volatile_acquirables\": { \"id_nums\": [], \"description\": \"List of all acquirables obtained this playthrough (only volatile)\"}, "

    # parse awareness
    awarenessJson = "\"awareness\": { \"level\": 0, \"description\": \"Player's level of awareness\"}, "

    # parse personality affinities
    personalityAffinitiesJson = "\"personality_affinities\": { \"affinities\": [0,0,0,0,0,0], \"description\": \"Player's personality affinity values\" }, "

    # parse triggered events
    triggeredEventsJson = parse_triggered_events(sections[7])

    # combine it all
    jsonStr = "{" + currentLocationJson + currentNpcJson + mindscapeCurrentLocationJson + mindscapeCurrentNpcJson + volatileAcquirablesJson + awarenessJson + personalityAffinitiesJson + triggeredEventsJson + "}"

    # check for accuracy
    check_for_accuracy(jsonStr)

    # dump to file
    jsonFilepath = filepath[:filepath.find('.')] + ".json"
    with open(jsonFilepath, 'w') as f:
        json.dump(jsonStr, f)




if __name__ == "__main__":
    filepath = os.getcwd() + "/resources/TextFiles/Templates/DefaultSavedGame.mao"
    convert_file(filepath)
