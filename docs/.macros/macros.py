import posixpath
import json

def define_env(env):

    @env.macro
    def crafting_recipe(id: str, header = True, footer = True):
        """Generates a table displaying the required materials and also a crafting table example.
        
        This function performs the following checks in order:

        - Looks for docs/assets/items/{namespace}/{item}.json with {namespace} and {item} being obtained from the id parameter.
        - Looks for a "crafting" object with a "ingredients" object
        
        Parameters:  
            id (str): ID of the item to display in the format namespace:id. Omitting namespace assumes the minecraft namespace.
            header (bool): Whether to include the <table>, <thead>, header rows and <tbody> tags in the table. (default True)
            footer (bool): Whether to include the </tbody> and </table> tags in the table. (default True)
        
        Returns:  
            String containing a Admonition warning div if something is missing, or the actual table.
        
        """
        if not id:
            return admo_warn("No ID specified.")
        
        item = get_item_path(id)

        if not item:
            return admo_warn("No Item found!")

        json_data = read_json(f"docs/assets/items/{item}.json")
        if not json_data:
            return admo_warn(f"ouldn't find <code>{item}.json</code> in <code>assets/items/</code>!")

        crafting = json_data.get("crafting")
        if not crafting:
            return admo_warn("No Crafting recipe found!")
        
        ingredients = crafting.get("ingredients")
        if not ingredients:
            return admo_warn("No ingredients specified!")

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
        
        unique_ingredients = {}
        ingredients_names = []

        for num in range(1, 10):
            ingredient_id = ingredients.get(f"{num}")
            if not ingredient_id or unique_ingredients.get(ingredient_id):
                continue

            item_path = get_item_path(ingredient_id)
            ingredient_data = read_json(f"docs/assets/items/{item_path}.json")

            if not ingredient_data:
                continue

            ingredients_names.append(ingredient_data["name"] if "name" in ingredient_data else ingredient_id)
            unique_ingredients[ingredient_id] = ingredient_data
        
        ingredients_names.sort()

        strings.extend([
            " + ".join(ingredients_names),
            "</td>",
            "<td>",
            '<div class="crafting-table tooltips">'
        ])

        for num in range(1, 10):
            ingredient_id = ingredients.get(f"{num}")
            if not ingredient_id or not unique_ingredients.get(ingredient_id):
                strings.append(f'<span class="invslot-item slot{num}"></span>')
                continue

            ingredient_data = unique_ingredients[ingredient_id]
            item_path = get_item_path(ingredient_id)


            item_slot = [
                f'<span class="{"animated " if isinstance(ingredient_data.get("variants"), list) else ""}invslot-item slot{num}" data-minetip-title="',
                ingredient_data.get("name", ingredient_id.replace("_", " ").title()),
                '"',
                f' data-minetip-text="{ingredient_data["lore"]}">' if "lore" in ingredient_data else ">"
            ]

            if isinstance(ingredient_data.get("variants"), list):
                for i, variant in enumerate(ingredient_data["variants"]):
                    item_slot.append(f'<img src="/assets/img/items/{get_item_path(variant)}.png" class="{"animated-active " if i == 0 else ""}no-glight" loading="lazy" alt="{ingredient_id}">')
            else:
                item_slot.append(f'<img src="/assets/img/items/{item_path}.{"gif" if "gif" in ingredient_data and ingredient_data["gif"] else "png"}" class="no-glight" loading="lazy" alt="{ingredient_id}">')
            
            item_slot.append('</span>')

            strings.append(''.join(item_slot))
        
        result_slot = [
            f'<span class="{"animated " if isinstance(json_data.get("variants"), list) else ""}invslot-item slot0" data-minetip-title="',
            json_data["name"] if "name" in json_data else id,
            '"',
            f' data-minetip-text="{json_data["lore"]}">' if "lore" in json_data else ">"
        ]

        if isinstance(json_data.get("variants"), list):
            for i, variant in enumerate(json_data["variants"]):
                result_slot.append(f'<img src="/assets/img/items/{get_item_path(variant)}.png" class="{"animated-active " if i == 0 else ""}no-glight" loading="lazy" alt="{id}">')
        else:
            result_slot.append(f'<img src="/assets/img/items/{item}.{"gif" if json_data.get("gif", False) else "png"}" class="no-glight" loading="lazy" alt="{id}">')
        
        result_slot.extend([
            f'<div class="quantity">{crafting["amount"]}</div>' if "amount" in crafting and crafting["amount"] > 1 else "",
            '</span>'
        ])

        strings.append(''.join(result_slot))

        strings.extend([
            f'<img src="/assets/img/recipes/arrow.png" class="arrow" alt="" draggable="false">',
            '<span class="shapeless" data-minetip-title="This recipe is shapeless">' if "shapeless" in crafting and crafting["shapeless"] else "",
            '<img src="/assets/img/recipes/shapeless.png" class="no-glight" alt="" draggable="false">' if "shapeless" in crafting and crafting["shapeless"] else "",
            "</span>" if "shapeless" in crafting and crafting["shapeless"] else "",
            "</div>",
            "</td>",
            "</tr>"
        ])

        if footer:
            strings.extend(['</tbody>', '</table>'])
        
        return '\n'.join(strings)

    @env.macro
    def smithing_recipe(id: str, header = True, footer = True):
        """Generates a table displaying the required materials and also a smithing recipe display.
        
        This function performs the following checks in order:

        - Looks for docs/assets/items/{namespace}/{item}.json with {namespace} and {item} being obtained from the id parameter.
        - Looks for a "smithing" object with a "template", "item" and "material" object
        
        Parameters:  
            id (str): ID of the item to display in the format namespace:id. Omitting namespace assumes the minecraft namespace.
            header (bool): Whether to include the <table>, <thead>, header rows and <tbody> tags in the table. (default True)
            footer (bool): Whether to include the </tbody> and </table> tags in the table. (default True)
        
        Returns:  
            String containing a Admonition warning div if something is missing, or the actual table.
        
        """
        if not id:
            return admo_warn("No ID specified!")
        
        result = get_item_path(id)

        if not result:
            return admo_warn("No Item found!")
        
        json_data = read_json(f"docs/assets/items/{result}.json")
        if not json_data:
            return admo_warn(f"Couldn't find <code>{result}.json</code> in <code>assets/items/</code>!")
        
        smithing = json_data.get("smithing")
        if not smithing:
            return admo_warn("No Smithing recipe found!")
        
        if not smithing.get("template"):
            return admo_warn("No Template specified!")
        
        if not smithing.get("item"):
            return admo_warn("No Item specified!")
        
        if not smithing.get("material"):
            return admo_warn("No Material specified!")

        template_path = get_item_path(smithing["template"])
        item_path = get_item_path(smithing["item"])
        material_path = get_item_path(smithing["material"])

        template = read_json(f"docs/assets/items/{template_path}.json")
        item = read_json(f"docs/assets/items/{item_path}.json")
        material = read_json(f"docs/assets/items/{material_path}.json")

        if not template:
            return admo_warn(f"No template item <code>{template_path}</code> found in <code>/assets/items/</code>!")
        
        if not item:
            return admo_warn(f"No item <code>{item_path}</code> found in <code>/assets/items/</code>!")
        
        if not material:
            return admo_warn(f"No material <code>{material_path}</code> found in <code>/assets/items/</code>!")

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
                template["name"],
                item["name"],
                material["name"]
            ]),
            "</td>",
            "<td>",
            '<div class="smithing tooltips">',
            ''.join([
                f'<span class="invslot-item slot0" data-minetip-title="{json_data["name"]}"',
                f' data-minetip-text="{json_data["lore"]}">' if "lore" in json_data else ">",
                f'<img src="/assets/img/items/{result}.png" class="no-glight" loading="lazy" alt="{id}">',
                "</span>"
            ]),
            ''.join([
                f'<span class="invslot-item slot1" data-minetip-title="{template["name"]}"',
                f' data-minetip-text="{template["lore"]}">' if "lore" in template else ">",
                f'<img src="/assets/img/items/{template_path}.png" class="no-glight" loading="lazy" alt="{template_path.replace("/", ":")}">',
                "</span>"
            ]),
            ''.join([
                f'<span class="invslot-item slot2" data-minetip-title="{item["name"]}"',
                f' data-minetip-text="{item["lore"]}">' if "lore" in item else ">",
                f'<img src="/assets/img/items/{item_path}.png" class="no-glight" loading="lazy" alt="{item_path.replace("/", ":")}">',
                "</span>"
            ]),
            ''.join([
                f'<span class="invslot-item slot3" data-minetip-title="{material["name"]}"',
                f' data-minetip-text="{material["lore"]}">' if "lore" in material else ">",
                f'<img src="/assets/img/items/{material_path}.png" class="no-glight" loading="lazy" alt="{material_path.replace("/", ":")}">',
                "</span>"
            ]),
            '<img src="/assets/img/recipes/arrow.png" alt="" class="arrow" draggable="false">'
            "</div>",
            "</td>",
            "</tr>"
        ])

        if footer:
            strings.extend([
                "</tbody>",
                "</table>"
            ])
        
        return '\n'.join(strings)
    
    @env.macro
    def smelting_recipe(id: str, header = True, footer = True):
        """Generates a table displaying the required materials and also a smelting recipe display.
        
        This function performs the following checks in order:

        - Looks for docs/assets/items/{namespace}/{item}.json with {namespace} and {item} being obtained from the id parameter.
        - Looks for a "smelting" object with a "item" object
        
        Parameters:  
            id (str): ID of the item to display in the format namespace:id. Omitting namespace assumes the minecraft namespace.
            header (bool): Whether to include the <table>, <thead>, header rows and <tbody> tags in the table. (default True)
            footer (bool): Whether to include the </tbody> and </table> tags in the table. (default True)
        
        Returns:  
            String containing a Admonition warning div if something is missing, or the actual table.
        
        """
        if not id:
            return admo_warn("No ID specified!")
        
        result = get_item_path(id)

        if not result:
            return admo_warn("No result Item found!")
        
        json_data = read_json(f"docs/assets/items/{result}.json")
        if not json_data:
            return admo_warn(f"Couldn't find <code>{result}.json</code> in <code>assets/items/</code>!")
        
        smelting = json_data.get("smelting")
        if not smelting:
            return admo_warn("No Smelting recipe found!")
        
        item_path = get_item_path(smelting["item"])

        item = read_json(f"docs/assets/items/{item_path}.json")

        if not item:
            return admo_warn(f"No item <code>{template_path}</code> found in <code>/assets/items/</code>!")

        strings = [
            '<table>',
            '<thead>',
            '<tr>',
            '<th>Ingredients</th>',
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

        strings.extend([
            item["name"] if "name" in item else "Unknown Item",
            "</td>",
            "<td>",
            '<div class="furnace tooltips">',
            ''.join([
                f'<span class="invslot-item slot0" data-minetip-title="{json_data["name"]}"',
                f' data-minetip-text="{json_data["lore"]}">' if "lore" in json_data else ">",
                f'<img src="/assets/img/items/{result}.png" class="no-glight" loading="lazy" alt="{id}">',
                "</span>"
            ]),
            ''.join([
                f'<span class="invslot-item slot1" data-minetip-title="{item["name"]}"',
                f' data-minetip-text="{item["lore"]}">' if "lore" in item else ">",
                f'<img src="/assets/img/items/{item_path}.png" class="no-glight" loading="lazy" alt="">',
                "</span>"
            ]),
            '<span class="invslot-item slot2"></span>'
            '<img src="/assets/img/recipes/fire.gif" alt="fire" class="fire" draggable="false">',
            '<img src="/assets/img/recipes/arrow.gif" alt="arrow" class="arrow" draggable="false">',
            f'<span class="exp">{smelting.get("exp", 0.0)} XP</span>',
            f'<span class="time">{smelting.get("time", 10)}s</span>',
            "</div>"
            "</td>",
            "</tr>"
        ])

        if footer:
            strings.extend([
                "</tbody>",
                "</table>"
            ])
        
        return '\n'.join(strings)
    
    @env.macro
    def infobox(id: str):
        if not id:
            return admo_warn("No ID specified!")
        
        result = get_item_path(id)

        if not result:
            return admo_warn("No result item found!")
        
        json_data = read_json(f"docs/assets/items/{result}.json")
        if not json_data:
            return admo_warn(f"Couldn't find <code>{result}.json</code> in <code>assets/items/</code>!")

        strings = [
            '<div style="float: right; margin-left: .75rem;">',
            '<table>',
            '<thead>',
            '<tr>',
            f'<th style="text-align: center;" colspan="2">{json_data["name"] if "name" in json_data else env.get("page", {}).get("title", "")}</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td colspan="2">',
            f'<img src="/assets/img/items/{f"icons/{json_data["icon"]}" if "icon" in json_data else f"{result}.png"}" style="max-width: 250px;">',
            '</td>',
            '</tr>'
        ]

        if isinstance(json_data.get("attributes"), dict):
            for key, value in json_data["attributes"].items():
                strings.append('<tr>')
                if key.lower() == "stack_size":
                    strings.append('<td><b>Stackable</b></td>')
                else:
                    strings.append(f'<td><b>{key.replace("_", " ").title()}</b></td>')
                
                if isinstance(value, dict):
                    values = []
                    for vKey, vValue in value.items():
                        values.append(f"{vKey}: {vValue}")
                    
                    strings.append(f'<td>{"<br>".join(values)}')
                elif isinstance(value, list):
                    strings.append(f'<td>{"<br>".join(value)}')
                else:
                    if key.lower() == "stack_size" and isinstance(value, int):
                        strings.append(f'<td>{f"Yes ({value})" if value > 1 else "No"}')
                    else:
                        strings.append(f'<td>{value}</td>')
        
        strings.extend([
            '</tbody>',
            '</table>',
            '</div>'
        ])

        return "\n".join(strings)

    def get_item_path(item: str):
        """Takes the provided item string and converts it from {namespace}:{id} to {namespace}/{id}.  
        Should no colon be present will it assume no namespace and return minecraft/{item} instead.
        
        Providing None returns None.
        
        """
        if not item:
            return None
            
        if ":" in item:
            return item.replace(":", "/")
        else:
            return f"minecraft/{item}"
    
    def read_json(file_path: str):
        """Takes the provided file_path, appends it to the project's directory and tries to load it as a JSON.

        Should the load fail due to a FileNotFoundError will None be returned.
        
        """
        path = posixpath.sep.join([env.project_dir, file_path])

        try:
            with open(path, 'r', encoding='utf-8') as file:
                return json.load(file)
        except FileNotFoundError:
            return None
    
    def admo_warn(text: str):
        return f'<div class="admonition warning"><p class="admonition-title">{text}</p></div>'