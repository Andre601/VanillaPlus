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
            return "<div class=\"admonition warning\"><p class=\"admonition-title\">No id specified!</p></div>"
        
        item = get_item_path(id)

        if not item:
            return "<div class=\"admonition warning\"><p class=\"admonition-title\">No item found!</p></div>"

        json_data = read_json(f"docs/assets/items/{item}.json")
        if not json_data:
            return f"<div class=\"admonition warning\"><p class=\"admonition-title\">Couldn't find <code>{item}.json</code> in <code>assets/items/</code>!</p></div>"

        crafting = json_data.get("crafting")
        if not crafting:
            return "<div class=\"admonition warning\"><p class=\"admonition-title\">No crafting recipe found!</p></div>"
        
        ingredients = crafting.get("ingredients")
        if not ingredients:
            return '<div class="admonition warning"><p class="admonition-title">No ingredients specified!</p></div>'

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
        
        create_recipe = crafting.get("create_recipe", False)
        unique_ingredients = {}
        ingredients_names = []

        for num in range(1, (13 if create_recipe else 10)):
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

        strings.extend([" + ".join(ingredients_names), "</td>", "<td>"])
        strings.append(f'<div class="crafting-table tooltips">')

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

        if create_recipe:
            strings.append(''.join([
                '<span class="invslot-item slot-crafter" data-minetip-title="Requires 12 Mechanical Crafters">',
                '<img src="/assets/img/items/create/mechanical_crafter.png" class="no-glight" loading="lazy" alt="" draggable="false">',
                '<div class="quantity">12</div>',
                '</span>'
            ]))

        strings.extend([
            f'<img src="/assets/img/recipes/{"create-arrow" if create_recipe else "arrow"}.png" class="arrow" alt="" draggable="false">',
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
            return '<div class="admonition warning"><p class="admonition-title">No id specified!</p></div>'
        
        result = get_item_path(id)

        if not result:
            return '<div class="admonition warning"><p class="admonition-title">No result item found!</p></div>'
        
        json_data = read_json(f"docs/assets/items/{result}.json")
        if not json_data:
            return f'<div class="admonition warning"><p class="admonition-title">Couldn\'t find <code>{result}.json</code> in <code>assets/items/</code>!</p></div>'
        
        smithing = json_data.get("smithing")
        if not smithing:
            return '<div class="admonition warning"><p class="admonition-title">No smithing recipe found!</p></div>'
        
        if not smithing.get("template"):
            return '<div class="admonition warning"><p class="admonition-title">No template specified!</p></div>'
        
        if not smithing.get("item"):
            return '<div class="admonition warning"><p class="admonition-title">No item specified!</p></div>'
        
        if not smithing.get("material"):
            return '<div class="admonition warning"><p class="admonition-title">No material specified!</p></div>'

        template_path = get_item_path(smithing["template"])
        item_path = get_item_path(smithing["item"])
        material_path = get_item_path(smithing["material"])

        template = read_json(f"docs/assets/items/{template_path}.json")
        item = read_json(f"docs/assets/items/{item_path}.json")
        material = read_json(f"docs/assets/items/{material_path}.json")

        if not template:
            return f'<div class="admonition warning"><p class="admonition-title">No template item <code>{template_path}</code> found in <code>/assets/items/</code>!</p></div>'
        
        if not item:
            return f'<div class="admonition warning"><p class="admonition-title">No template item <code>{item_path}</code> found in <code>/assets/items/</code>!</p></div>'
        
        if not material:
            return f'<div class="admonition warning"><p class="admonition-title">No template item <code>{material_path}</code> found in <code>/assets/items/</code>!</p></div>'

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
            return '<div class="admonition warning"><p class="admonition-title">No id specified!</p></div>'
        
        result = get_item_path(id)

        if not result:
            return '<div class="admonition warning"><p class="admonition-title">No result item found!</p></div>'
        
        json_data = read_json(f"docs/assets/items/{result}.json")
        if not json_data:
            return f'<div class="admonition warning"><p class="admonition-title">Couldn\'t find <code>{result}.json</code> in <code>assets/items/</code>!</p></div>'
        
        smelting = json_data.get("smelting")
        if not smelting:
            return '<div class="admonition warning"><p class="admonition-title">No smelting recipe found!</p></div>'
        
        item_path = get_item_path(smelting["item"])

        item = read_json(f"docs/assets/items/{item_path}.json")

        if not item:
            return f'<div class="admonition warning"><p class="admonition-title">No item <code>{template_path}</code> found in <code>/assets/items/</code>!</p></div>'

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
            return '<div class="admonition warning"><p class="admonition-title">No id specified!</p></div>'
        
        result = get_item_path(id)

        if not result:
            return '<div class="admonition warning"><p class="admonition-title">No result item found!</p></div>'
        
        json_data = read_json(f"docs/assets/items/{result}.json")
        if not json_data:
            return f'<div class="admonition warning"><p class="admonition-title">Couldn\'t find <code>{result}.json</code> in <code>assets/items/</code>!</p></div>'
        
        icon = result + (".gif" if json_data.get("gif", False) else ".png")
        
        if json_data.get("icon", None):
            icon = f"icon/{json_data["icon"]}"

        strings = [
            '<div style="float: right; margin-left: .75rem;">',
            '<table>',
            '<thead>',
            '<tr>',
            f'<th style="text-align: center;" colspan="2">{json_data["name"] if json_data["name"] else id.replace("_", " ").title()}</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td colspan="2">',
            f'<img src="/assets/img/items/{icon}" style="max-width: 250px;">',
            '</td>',
            '</tr>'
        ]

        if isinstance(json_data.get("durability"), (dict, int)):
            strings.extend([
                '<tr>',
                '<td><b>Durability</b></td>'
            ])
            durability = json_data["durability"]
            if isinstance(durability, dict):
                values = []
                for key, value in durability.items():
                    values.append(f"{key}: {value}")

                strings.append(f'<td>{"<br>".join(values)}')
            else:
                strings.append(f'<td>{durability}</td>')
            
            strings.append('</tr>')
        
        strings.extend([
            '<tr>',
            '<td><b>Stackable</b></td>'
        ])

        if isinstance(json_data.get("stack_size"), int) and json_data["stack_size"] > 1:
            strings.append(f'<td>Yes ({json_data["stack_size"]})</td>')
        else:
            strings.append('<td>No</td>')
        
        strings.append('</tr>')

        if json_data.get("tool"):
            strings.extend([
                '<tr>',
                '<td><b>Tool</b></td>',
                f'<td>{json_data["tool"]}</td>',
                '</tr>'
            ])

        if json_data.get("blast_resistance"):
            strings.extend([
                '<tr>',
                '<td><b>Blast resistance</b></td>',
                f'<td>{json_data["blast_resistance"]}</td>',
                '</tr>'
            ])

        if json_data.get("hardness"):
            strings.extend([
                '<tr>',
                '<td><b>Hardness</b></td>',
                f'<td>{json_data["hardness"]}</td>',
                '</tr>'
            ])

        if (isinstance(json_data.get("hunger"), int) and json_data.get("hunger", 0) > 0) or (isinstance(json_data.get("saturation"), int) and json_data.get("saturation", 0) > 0):
            hunger = json_data["hunger"] or 0
            saturation = json_data["saturation"] or 0
            strings.extend([
                '<tr>',
                '<td><b>Restores</b></td>',
                f'<td>Hunger: {hunger}<br>Saturation: {saturation}</td>',
                '</tr>'
            ])
        
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