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



def convert_glyphs_block_to_json(block: List[str]) -> str:
    # parse id
    id_start = block[0].find('/')+1
    id_end = block[0].find(']')
    id_num = block[0][id_start:id_end]
    glyphJson = "\"" + id_num + "\": {"
    # parse title
    title_start = block[1].find('*')+1
    title_end = block[4].find('*')
    title = block[1][title_start:] + block[3][block[3].find('\t')+1:] + block[4][block[4].find('\t')+1:title_end]
    title = escape_special_chars(title)
    glyphJson += "\"title\": \"" + title + "\","
    # parse subtitle
    subtitle_start = block[5].find('@')+1
    subtitle_end = block[5].find('@', subtitle_start)
    subtitle = block[5][subtitle_start:subtitle_end]
    glyphJson += "\"subtitle\": \"" + subtitle + "\","
    # parse img
    img_start = block[6].find('$')+1
    img_end = block[6].find('$', img_start)
    img = block[6][img_start:img_end]
    glyphJson += "\"img\": \"" + img + "\","
    # parse description
    desc_start = block[7].find('#')+1
    desc_end = block[-2].find('#')
    desc = block[7][desc_start:]
    ind = 8
    while '#' not in block[ind]:
        desc += block[ind]
        ind += 1
    desc += block[-2][:desc_end]
    desc = escape_special_chars(desc)
    glyphJson += "\"description\": \"" + desc + "\""
    glyphJson += "}, "

    return glyphJson



def convert_glyphs(glyphsMao: List[str]) -> str:
    # aggregate into address-specific, self-contained blocks
    glyphsBlocks = parse_blocks(glyphsMao)
    # convert to JSON
    glyphsJson = "{"
    for glyphsBlock in glyphsBlocks:
        glyphsJson += convert_glyphs_block_to_json(glyphsBlock)
    glyphsJson = glyphsJson[:-2] + "}" # trim trailing comma

    print(glyphsJson[750:775])
    # check for accuracy
    check_for_accuracy(glyphsJson)

    return glyphsJson



def parse_sections(contents: List[str]) -> Tuple[List[str], List[str], List[str]]:
    glyphs = contents[:contents.index("__TOMES__\n")-1]
    tomes = contents[contents.index("__TOMES__\n"):contents.index("__MIND NPCs__\n")-1]
    mindNpcs = contents[contents.index("__MIND NPCs__\n"):]

    return glyphs, tomes, mindNpcs



def convert_file(filepath: str) -> None:
    with open(filepath) as f:
        contents = f.readlines()

    glyphsMao, tomesMao, mindNpcsMao = parse_sections(contents)

    glyphsJson = convert_glyphs(glyphsMao)



if __name__ == "__main__":
    filepath = os.getcwd() + "/resources/TextFiles/NonDialogueText/AcquirableDescriptions.mao"
    convert_file(filepath)
