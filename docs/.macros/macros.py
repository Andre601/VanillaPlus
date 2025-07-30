import posixpath
import json

def define_env(env):
    
    @env.macro
    def crafting(item: str, header = True, footer = True):
        if not item:
            return "ERROR: No Item Path specified"
        
        json_data = read_json("docs/assets/data.json")
        
        if not "crafting" in json_data:
            return "ERROR: No 'crafting' section found in data.json!"
        
        if not "items" in json_data:
            return "ERROR: No 'items' section found in data.json!"
        
        recipes = json_data["crafting"]
        if not item in recipes:
            return f"ERROR: No Crafting recipe for item '{item}' found!"
        
        recipe = recipes[item]
        if not "result" in recipe:
            return "ERROR: Recipe does not contain a 'result' option!"
        
        items = json_data["items"]
        
        strings = [
            '<table>',
            '<thead>',
            '<tr>',
            '<th>Ingredients</th>',
            '<th>Crafting recipe</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td>'
        ] if header else [
            '<tr>',
            '<td>'
        ]
        
        ingredients_str = []
        unique_ingredients = []
        for num in range(1, 10):
            name = recipe.get(f"slot{num}")
            if not name or name in unique_ingredients:
                continue
            
            ingredient = items.get(recipe.get(f"slot{num}"))
            if not ingredient:
                continue
            
            ingredients_str.append(get_ingredient_text(ingredient))
            unique_ingredients.append(name)
        
        ingredients_str.sort()
        
        strings.extend([' + '.join(ingredients_str), '</td>', '<td>'])
        strings.append('<div class="crafting-table tooltips">')
        
        for num in range(10):
            if num == 0:
                strings.append(f'{get_slot_html(recipe["result"], items, 0, recipe["quantity"] if "quantity" in recipe else 1)}')
                continue
            
            strings.append(f'{get_slot_html(recipe.get(f"slot{num}"), items, num)}')
        
        strings.extend([
            '<img src="/assets/img/recipes/arrow.png" class="arrow" alt="" draggable="false">',
            '<span class="shapeless" data-minetip-title="This recipe is shapeless">' if "shapeless" in recipe and recipe["shapeless"] else '',
            '<img src="/assets/img/recipes/shapeless.png" alt="" draggable="false">' if "shapeless" in recipe and recipe["shapeless"] else '',
            '</span>' if "shapeless" in recipe and recipe["shapeless"] else '',
            '</div>',
            '</td>',
            '</tr>'
        ])
        
        if footer:
            strings.extend(['</tbody>', '</table>'])
        
        return '\n'.join(strings)
    
    @env.macro
    def smelting(result: str, header = True, footer = True):
        json_data = read_json("docs/assets/data.json")
        
        if not "smelting" in json_data:
            return "ERROR: No 'smelting' section found in data.json."
        
        if not "items" in json_data:
            return "ERROR: No 'items' section found in data.json!"
        
        recipes = json_data["smelting"]
        if not result in recipes:
            return f"ERROR: No smelting recipe found for item '{result}'!"
        
        recipe = recipes[result]
        if not "result" in recipe:
            return "ERROR: Smelting recipe does not contain a 'result' value!"
        
        items = json_data["items"]
        
        strings = [
            '<table>',
            '<thead>',
            '<tr>',
            '<th>Ingredient</th>',
            '<th>Smelting recipe</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td>'
        ] if header else [
            '<tr>',
            '<td>'
        ]
        
        ingredient = items.get(recipe.get("ingredient"))
        
        strings.extend([
            f'{get_ingredient_text(ingredient)}',
            '</td>',
            '<td>'
            '<div class="furnace tooltips">'
            f'{get_slot_html(recipe["result"], items, 0)}',
            f'{get_slot_html(recipe["ingredient"], items, 1)}',
            '<img src="/assets/img/recipes/fire.gif" alt="" class="fire" draggable="false">',
            f'{get_slot_html("fuel", items, 2)}',
            '<img src="/assets/img/recipes/arrow.gif" class="arrow" draggable="false">',
            f'<span class="exp">Exp: {recipe.get("exp", "0.0")}</span>',
            '</div>',
            '</td>',
            '</tr>'
        ])
        
        if footer:
            strings.extend([
                '</tbody>',
                '</table>'
            ])
        
        return '\n'.join(strings)
    
    @env.macro
    def smithing(result: str, header = True, footer = True):
        json_data = read_json("docs/assets/data.json")
        
        if not "smithing" in json_data:
            return "ERROR: No 'smithing' section found in data.json!"
        
        if not "items" in json_data:
            return "ERROR: No 'items' section found in data.json!"
        
        recipes = json_data["smithing"]
        if not result in recipes:
            return f"ERROR: No smithing recipe found for item '{result}'!"
        
        recipe = recipes[result]
        if not "result" in recipe:
            return "ERROR: Smithing recipe does not contain a 'result' value!"
        
        items = json_data["items"]
        
        strings = [
            '<table>',
            '<thead>',
            '<tr>',
            '<th>Ingredients</th>',
            '<th>Smithing recipe</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td>'
        ] if header else [
            '<tr>',
            '<td>'
        ]
        
        strings.extend([
            ' + '.join([
                f'{get_ingredient_text(items.get(recipe.get("base")))}',
                f'{get_ingredient_text(items.get(recipe.get("template")))}',
                f'{get_ingredient_text(items.get(recipe.get("addition")))}'
            ]),
            '</td>',
            '<td>',
            '<div class="smithing tooltips">',
            f'{get_slot_html(recipe["result"], items, 0)}',
            f'{get_slot_html(recipe["base"], items, 1)}',
            f'{get_slot_html(recipe["template"], items, 2)}',
            f'{get_slot_html(recipe["addition"], items, 3)}',
            '<img src="/assets/img/recipes/arrow.png" alt="" class="arrow" draggable="false">',
            '</div>',
            '</td>',
            '</tr>'
        ])
        
        if footer:
            strings.extend([
                '</tbody>',
                '</table>'
            ])
        
        return '\n'.join(strings)
            
    def get_ingredient_text(item) -> str:
        if not item:
            return "Unknown Item"
        
        if not "link" in item:
            return item["title"]
        
        return f'<a href="/wiki/{item["link"]}">{item["title"]}</a>'
    
    def get_slot_html(item: str, json_data, slot: int, quantity: int = 1):
        if not item:
            return f'<span class="invslot-item slot{slot}"></span>'
        
        item_data = json_data[item] if item else None
        if not item_data:
            return f'<span class="invslot-item slot{slot}"></span>'
        
        result = [
            f'<span class="invslot-item slot{slot}" data-minetip-title="',
            item_data["title"] if "title" in item_data else "Unknown Item",
            '"',
            f' data-minetip-text="{item_data["lore"]}">' if "lore" in item_data else '>',
            f'<a href="/wiki/{item_data["link"]}">' if "link" in item_data and slot > 0 else '',
            '<img src="/assets/img/',
            item_data["img"] if "img" in item_data else "blocks/unknown.png",
            '" class="no-glight" alt="">',
            '</a>' if "link" in item_data and slot > 0 else '',
            f'<div class="quantity">{quantity}</div>' if quantity > 1 else '',
            '</span>'
        ]
        
        return ''.join(result)
    
    def read_json(file_path: str):
        """
        Reads a provided JSON path and converts it to a usable Jinja2 object structure.
        """
        path = posixpath.sep.join([env.project_dir, file_path])
        with open(path, 'r', encoding='utf-8') as file:
            return json.load(file)
