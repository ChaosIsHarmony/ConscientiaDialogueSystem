import os
import glob
import json
from typing import List, Tuple


def check_for_accuracy(json_str: str) -> None:
    '''
    Pretty print the JSON string to confirm proper formatting.
    Will raise exceptions if it cannot parse the string due to incorrect formatting.
    '''
    json_obj = json.loads(json_str)
    parsed_json_str = json.dumps(json_obj, indent=2)
    print(parsed_json_str)



def parse_blocks(rawSectionLines: List[str]) -> List[List[str]]:
    '''
    Aggregates the lines relevant to an address specific block.
    '''
    blocks = []
    i = 0
    while i < len(rawSectionLines):
        block = []
        if "[/" in rawSectionLines[i]:
            while not "/]" in rawSectionLines[i]:
                block.append(rawSectionLines[i])
                i += 1
            block.append(rawSectionLines[i])
        else:
            i += 1
            continue

        if len(block) > 0:
            blocks.append(block)

    return blocks



def convert_npc_switchers(npcSwitchersMao: List[str]) -> str:
    # aggregate into address-specific, self-contained blocks
    npcSwitchersBlocks = parse_blocks(npcSwitchersMao)
    # convert to JSON string
    npcSwitchersJson = "{"
    for block in npcSwitchersBlocks:
        # parse address
        add_start = block[0].find('/')+1
        add_end = block[0].find(']')
        address = block[0][add_start:add_end]
        npcSwitchersJson += "\"" + address + "\":"
        # parse npc id
        npc_id_start = block[1].find(':')+1
        npc_id_end = block[1].find('|', npc_id_start)
        npc_id = block[1][npc_id_start:npc_id_end]
        npcSwitchersJson += npc_id + ", "

    if len(npcSwitchersJson) > 1:
        npcSwitchersJson = npcSwitchersJson[:-2] + "}" # trm trailing comma
    else:
        npcSwitchersJson = "{}" # Unfinished dialogue file OR no switchers

    # check for accuracy
    #  check_for_accuracy(npcSwitchersJson)

    return npcSwitchersJson



def convert_fighting_words(fightingWordsMao: List[str]) -> str:
    # aggregate into address-specific, self-contained blocks
    fightingWordsBlocks = parse_blocks(fightingWordsMao)
    # convert to JSON string
    fightingWordsJson = "{ \"addresses\": ["
    for block in fightingWordsBlocks:
        add_start = block[0].find('/')+1
        add_end = block[0].find(']')
        address = block[0][add_start:add_end]
        fightingWordsJson += "\"" + address +"\", "

    if len(fightingWordsJson) > 20:
        fightingWordsJson = fightingWordsJson[:-2] + "]}" # trim trailing comma
    else:
        fightingWordsJson = "{}" # Unfinished dialogue file OR no fighting

    # check for accuracy
    #  check_for_accuracy(fightingWordsJson)

    return fightingWordsJson



def convert_events(eventsMao: List[str]) -> str:
    # aggregate into address-specific, self-contained blocks
    eventBlocks = parse_blocks(eventsMao)
    # build JSON string
    eventsJson = "{"
    for block in eventBlocks:
        # indexes
        event_start = block[1].find('|')+1
        event_mid = block[1].find(':')
        event_end = block[1].find('|', event_start)

        # parse address
        address = block[0][block[0].find('/')+1:block[0].find(']')]
        eventsJson += "\"" + address + "\": {"
        # parse event
        event_num = block[1][event_start:event_mid]
        eventsJson += "\"event_num\": " + event_num + ", "
        # parse destination address
        event_dest_add = block[1][event_mid+1:event_end]
        eventsJson += "\"dest_add\": \"" + event_dest_add +"\"}, "

    if len(eventsJson) > 1:
        eventsJson = eventsJson[:-2] + "}" # trim trailing comma
    else:
        eventsJson = "{}" # Unfinished dialogue file OR file with no events

    # check for accuracy
    #  check_for_accuracy(eventsJson)

    return eventsJson


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


def parse_response(response: str) -> str:
    # parse personality
    pers_start = response.find('(')+1
    pers_end = response.find('#', pers_start)
    json_response = "\"" + response[pers_start:pers_end] + "\": {"

    # parse response text
    text_start = response.find('#', pers_end)+1
    text_end = response.find(')', text_start)
    json_response += "\"response_text\": \"" + escape_special_chars(response[text_start:text_end]) + "\", "

    # parse personality points
    pts_start = response.find(':', text_end)+1
    pts_end = response.find(',', pts_start)
    json_response += "\"points\": " + response[pts_start:pts_end] + ", "

    # parse destination address
    add_start = response.find(',', pts_end)+1
    add_end = response.find('}', add_start)
    json_response += "\"dest_add\": \"" + response[add_start:add_end] + "\"}, "

    return json_response

def convert_dialogue_block_to_json(block: List[str]) -> str:
    ADDRESS = 0
    ACTION = 1
    DIALOGUE = 2

    # parse address
    address = block[ADDRESS][block[ADDRESS].find('/')+1:block[ADDRESS].find(']')]
    dialogueJson = "\"" + address + "\": {"

    # parse special actions
    if ':' in block[ACTION]:
        dialogueJson += "\"action\": ["
        action_ind = block[ACTION].find('|')+1
        address_ind = block[ACTION].find(':')+1
        end_ind = block[ACTION].find('|', address_ind)
        # parse action type
        action_type = block[ACTION][action_ind:action_ind+1]
        dialogueJson += "\"" + action_type + "\", "
        if action_type == '^':
            event_num = block[ACTION][action_ind+1:address_ind-1]
            dialogueJson += event_num + ", "
        # parse action address
        action_address = block[ACTION][address_ind:end_ind]
        dialogueJson += "\"" + action_address + "\"], "

    # parse dialogue
    d_start_ind = block[DIALOGUE].find('*')+1
    d_end_ind = block[DIALOGUE].find('*', d_start_ind)
    end_of_dialogue_block = DIALOGUE
    if d_end_ind == -1:
        # abnormal dialogue, likely with \n inside
        # append all relevant dialogue strings to block[2]
        dialogue_str = block[DIALOGUE]
        end_of_dialogue_block += 1
        while "*" not in block[end_of_dialogue_block]:
            dialogue_str += block[end_of_dialogue_block]
            end_of_dialogue_block += 1
        dialogue_str += block[end_of_dialogue_block]
        block[DIALOGUE] = dialogue_str
        d_end_ind = block[DIALOGUE].find('*', d_start_ind) # reset end
    dialogue = block[DIALOGUE][d_start_ind:d_end_ind]

    # escape special chars
    if '\"' in dialogue or '\t' in dialogue or '\n' in dialogue:
        dialogue = escape_special_chars(dialogue)

    dialogueJson += "\"dialogue_text\": \"" + dialogue + "\", "

    # parse responses
    dialogueJson += "\"responses\": {"
    RESPONSES = end_of_dialogue_block + 1
    ind = RESPONSES
    while ind < len(block)-1:
        dialogueJson += parse_response(block[ind])
        ind += 1
    dialogueJson = dialogueJson[:-2] + "}" # trim trailing commas

    # close off JSON object
    dialogueJson += "}, "

    return dialogueJson



def convert_dialogue(dialogueMao: List[str]) -> str:
    """
    Takes in the .mao formatted dialogue section and return as a json formatted string.
    """
    # aggregate into address-specific, self-contained blocks
    dialogueBlocks = parse_blocks(dialogueMao)
    # convert to JSON
    dialogueJson = "{"
    for dialogueBlock in dialogueBlocks:
        dialogueJson += convert_dialogue_block_to_json(dialogueBlock)
    dialogueJson = dialogueJson[:-2] + "}" # trim trailing comma

    # check for accuracy
    #  check_for_accuracy(dialogueJson)

    return dialogueJson




def parse_sections(contents: List[str]) -> Tuple[List[str], List[str], List[str], List[str]]:
    dialogue = contents[:contents.index("TECHNICAL STUFF\n")-1]
    events = contents[contents.index("TECHNICAL STUFF\n"):contents.index("NPC SWITCHERS\n")-1]
    npcSwitchers = contents[contents.index("NPC SWITCHERS\n"):contents.index("FIGHTING WORDS\n")-1]
    fightingWords = contents[contents.index("FIGHTING WORDS\n")]

    return dialogue, events, npcSwitchers, fightingWords



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    dialogueMao, eventsMao, npcSwitchersMao, fightingWordsMao = parse_sections(contents)

    dialogueJson = convert_dialogue(dialogueMao)
    eventsJson = convert_events(eventsMao)
    npcSwitchersJson = convert_npc_switchers(npcSwitchersMao)
    fightingWordsJson = convert_fighting_words(fightingWordsMao)

    fileJson = "{ \"dialogue\": " + dialogueJson + ", \"events\": " + eventsJson + ", \"npc_switchers\": " + npcSwitchersJson + ", \"fighting_words\": " + fightingWordsJson  + "}"

    # check for accuracy
    #check_for_accuracy(fileJson)

    json_filepath = filepath[:filepath.index("/Original")] + "/JSON/" + filepath[filepath.index("/Dialogue"):filepath.index(".")] + ".json"
    print(json_filepath)
    with open(json_filepath, 'w') as f:
      json.dump(fileJson, f)




if __name__ == "__main__":
    filepaths = glob.glob(os.getcwd() + "/resources/TextFiles/Original/Dialogue/*/*.mao")

    for filepath in filepaths:
        print(filepath)
        convert_file(filepath)
