import os
import json
from typing import List, Tuple

def escape_special_chars(text: str) -> str:
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
    json_response += "\"text\": \"" + escape_special_chars(response[text_start:text_end]) + "\", "

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
    dialogueJson = dialogueJson[:-2] + "}" # to eliminate trailing commas

    # close off JSON object
    dialogueJson += "}, "

    return dialogueJson



def convert_dialogue(dialogueMao: List[str]) -> str:
    """
    Takes in the .mao formatted dialogue section and return as a json formatted string.
    """
    dialogueBlocks = []
    i = 0
    while i < len(dialogueMao):
        dialogueBlock = []
        if "[/" in dialogueMao[i]:
            while not "/]" in dialogueMao[i]:
                dialogueBlock.append(dialogueMao[i])
                i += 1
            dialogueBlock.append(dialogueMao[i])
        else:
            i += 1
            continue

        if len(dialogueBlock) > 0:
            dialogueBlocks.append(dialogueBlock)

    dialogueJson = "{"
    for dialogueBlock in dialogueBlocks:
        dialogueJson += convert_dialogue_block_to_json(dialogueBlock)
    dialogueJson = dialogueJson[:-2] + "}" # removing trailing comma


    print(dialogueJson[484100:484200])

    json_obj = json.loads(dialogueJson)
    json_str = json.dumps(json_obj, indent=2)
    print(json_str)


    return ""



def parse_sections(contents: List[str]) -> Tuple[List[str], List[str], List[str], List[str]]:
    dialogue = contents[:contents.index("TECHNICAL STUFF\n")-1]
    events = contents[contents.index("TECHNICAL STUFF\n"):contents.index("FIGHTING WORDS\n")-1]
    fightingWords = contents[contents.index("FIGHTING WORDS\n"):contents.index("NPC SWITCHERS\n")-1]
    npcSwitchers = contents[contents.index("NPC SWITCHERS\n"):]

    return dialogue, events, fightingWords, npcSwitchers



if __name__ == "__main__":
    filepath = "resources/TextFiles/Original/Dialogue/BookOfEidos/EIDOS_DAZIR.mao"
    with open(filepath) as f:
        contents = f.readlines()

    dialogueMao, eventsMao, fightingWordsMao, npcSwitchersMao = parse_sections(contents)

    dialogueJson = convert_dialogue(dialogueMao)
    print(len(dialogueJson))

