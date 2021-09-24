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



def parse_blocks(rawSectionLines: List[str]) -> List[List[str]]:
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



def convert_acq_block_to_json(block: List[str]) -> str:
    # parse id
    id_start = block[0].find('/')+1
    id_end = block[0].find(']')
    id_num = block[0][id_start:id_end]
    acqJson = "\"" + id_num + "\": {"

    # parse title
    title_start = block[1].find('*')+1
    title = block[1][title_start]
    ind = 2
    while '*' not in block[ind]:
        title += block[ind][block[ind].find('\t')+1:]
        ind += 1
    title += block[ind][:block[ind].find('*')]
    title = escape_special_chars(title)
    acqJson += "\"title\": \"" + title + "\","

    # parse subtitle
    SUBTITLE = ind + 1
    subtitle_start = block[SUBTITLE].find('@')+1
    subtitle_end = block[SUBTITLE].find('@', subtitle_start)
    subtitle = block[SUBTITLE][subtitle_start:subtitle_end]
    acqJson += "\"subtitle\": \"" + subtitle + "\","

    # parse img
    IMG = ind + 2
    img_start = block[IMG].find('$')+1
    img_end = block[IMG].find('$', img_start)
    img = block[IMG][img_start:img_end]
    acqJson += "\"img\": \"" + img + "\","

    # parse description
    DESC = ind + 3
    desc_start = block[DESC].find('#')+1
    desc = block[DESC][desc_start:]
    ind = DESC + 1
    while '#' not in block[ind]:
        desc += block[ind]
        ind += 1
    desc += block[ind][:block[ind].find('#')]
    desc = escape_special_chars(desc)
    acqJson += "\"description\": \"" + desc + "\""
    acqJson += "}, "

    return acqJson



def convert_acqs(acqsMao: List[str]) -> str:
    # aggregate into address-specific, self-contained blocks
    acqsBlocks = parse_blocks(acqsMao)
    # convert to JSON
    acqsJson = "{"
    for acqsBlock in acqsBlocks:
        acqsJson += convert_acq_block_to_json(acqsBlock)
    acqsJson = acqsJson[:-2] + "}" # trim trailing comma

    # check for accuracy
    #  check_for_accuracy(acqsJson)

    return acqsJson



def parse_sections(contents: List[str]) -> Tuple[List[str], List[str], List[str]]:
    glyphs = contents[:contents.index("__TOMES__\n")-1]
    tomes = contents[contents.index("__TOMES__\n"):contents.index("__MIND NPCs__\n")-1]
    mindNpcs = contents[contents.index("__MIND NPCs__\n"):]

    return glyphs, tomes, mindNpcs



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    glyphsMao, tomesMao, mindNpcsMao = parse_sections(contents)

    glyphsJson = convert_acqs(glyphsMao)
    tomesJson = convert_acqs(tomesMao)
    mindNpcsJson = convert_acqs(mindNpcsMao)

    jsonStr = "{\"glyphs\": " + glyphsJson + ", \"tomes\": " + tomesJson + ", \"mindNpcs\": " + mindNpcsJson + "}"

    # check for accuracy
    #  check_for_accuracy(jsonStr)

    jsonFilepath = filepath[:filepath.find('.')] + ".json"
    with open(jsonFilepath, 'w') as f:
        json.dump(jsonStr, f)


if __name__ == "__main__":
    filepath = os.getcwd() + "/resources/TextFiles/NonDialogueText/AcquirableDescriptions.mao"
    convert_file(filepath)
