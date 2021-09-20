import os
from typing import List, Tuple

def parse_sections(contents: List[str]) -> Tuple[List[str], List[str], List[str], List[str]]:
    dialogue = contents[:contents.index("TECHNICAL STUFF\n")-1]
    events = contents[contents.index("TECHNICAL STUFF\n"):contents.index("FIGHTING WORDS\n")-1]
    fightingWords = contents[contents.index("FIGHTING WORDS\n"):contents.index("NPC SWITCHERS\n")-1]
    npcSwitchers = contents[contents.index("NPC SWITCHERS\n"):]

    return dialogue, events, fightingWords, npcSwitchers



def escape_quotations_marks(text: str) -> str:
    inds_of_quotes = []
    # find index of quotes
    for i in range(len(text)):
        if text[i] == '\"':
            # add len to compensate for shift due to  adding escape slash
            inds_of_quotes.append(i+len(inds_of_quotes))
    # insert escape slash
    for i in inds_of_quotes:
        text = text[:i] + "\\" + text[i:]

    return text


def convertDialogueBlockToJson(block: List[str]) -> str:
    ADDRESS = 0
    ACTION = 1
    DIALOGUE = 2

    dialogueJson = "{"
    # parse address
    address = block[ADDRESS][block[ADDRESS].find('/')+1:block[ADDRESS].find(']')]
    dialogueJson += "\"address\": \"" + address + "\","

    # parse special actions
    if ':' in block[ACTION]:
        dialogueJson += "\"action\": ["
        action_ind = block[ACTION].find('|')+1
        address_ind = block[ACTION].find(':')+1
        end_ind = block[ACTION].find('|', address_ind)
        # parse action type
        action_type = block[ACTION][action_ind:action_ind+1]
        dialogueJson += "\"" + action_type + "\","
        if action_type == '^':
            event_num = block[ACTION][action_ind+1:address_ind-1]
            dialogueJson += event_num + ","
        # parse action address
        action_address = block[ACTION][address_ind:end_ind]
        dialogueJson += "\"" + action_address + "\"],"

    # parse dialogue
    d_start_ind = block[DIALOGUE].find('*')+1
    d_end_ind = block[DIALOGUE].find('*', d_start_ind)
    end_of_dialogue_block = DIALOGUE
    if d_end_ind == -1:
        # abnormal dialogue, likely with \n inside
        # append all relevant dialogue strings to block[2]
        dialogue_str = block[DIALOGUE]
        end_of_dialogue_block = DIALOGUE + 1
        while "*" not in block[end_of_dialogue_block]:
            dialogue_str += block[end_of_dialogue_block]
            end_of_dialogue_block += 1
        dialogue_str += block[end_of_dialogue_block]
        block[DIALOGUE] = dialogue_str
    # escape quotation marks
    if '\"' in block[DIALOGUE]:
        block[DIALOGUE] = escape_quotations_marks(block[DIALOGUE])
        d_end_ind = block[DIALOGUE].find('*', d_start_ind) # reset end

    dialogueJson += "\"dialogue\": \"" + block[DIALOGUE][d_start_ind:d_end_ind] + "\","

    RESPONSES = end_of_dialogue_block + 1
    dialogueJson += "}"
    print(dialogueJson)

    return dialogueJson



def convertDialogue(dialogueMao: List[str]) -> str:
    """
    Takes in a .mao formatted file and returns a json string.
    """
    dialogueBlocks = []
    for i, line in enumerate(dialogueMao):
        dialogueBlock = []
        if "[/" in dialogueMao[i]:
            while not "/]" in dialogueMao[i]:
                dialogueBlock.append(dialogueMao[i])
                i+= 1
            dialogueBlock.append(dialogueMao[i])

        if len(dialogueBlock) > 0:
            dialogueBlocks.append(dialogueBlock)

    dialogueJson = []
    for dialogueBlock in dialogueBlocks:
        dialogueJson.append(convertDialogueBlockToJson(dialogueBlock))

    print(dialogueJson[1])

    return ""



if __name__ == "__main__":
    filepath = "resources/TextFiles/Original/Dialogue/BookOfEidos/EIDOS_DAZIR.mao"
    with open(filepath) as f:
        contents = f.readlines()

    dialogueMao, eventsMao, fightingWordsMao, npcSwitchersMao = parse_sections(contents)

    print(len(dialogueMao))
    dialogueJson = convertDialogue(dialogueMao)
    print(len(dialogueMao))

