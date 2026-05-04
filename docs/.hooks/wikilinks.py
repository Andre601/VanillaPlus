#
#
#
#
#
#
import re
import posixpath
import logging
from pathlib import Path
from properdocs.plugins import get_plugin_logger

WIKILINK_PATTERN = re.compile(r"\[\[([^|\]]+)(?:\|([^\]]+))?]]")

log = get_plugin_logger(__name__)

pages_map = {}
interwiki = {}
aliases = {}

def on_config(config):
    global interwiki
    global aliases
    interwiki = config.get("extra", {}).get("interwiki", {})
    aliases = config.get("extra", {}).get("aliases", {})

    return config

def on_files(files, config):
    global pages_map

    pages_map = {}

    for file in files:
        if file.src_uri.endswith(".md"):
            name = file.src_uri.rsplit("/", 1)[-1].replace(".md", "")
            pages_map.setdefault(name.lower(), []).append(file)
    
    return files

def resolve_internal(name, current_file):
    candidates = pages_map.get(name.lower().replace(" ", "_"))
    if not candidates:
        return None
    
    current_dir = posixpath.dirname(current_file.src_uri)

    def distance(file):
        target_dir = posixpath.dirname(file.src_uri)
        rel = posixpath.relpath(target_dir, current_dir)
        return rel.count("../")
    
    return sorted(candidates, key=distance)[0]

def make_relative(current_file, target_file):
    src_dir = posixpath.dirname(current_file.src_uri)
    target = target_file.src_uri

    return posixpath.relpath(target, src_dir)

def check_aliases(target):
    for key, value in aliases.items():
        if key.lower() == target.lower():
            return value
    
    return None

def on_page_markdown(markdown, page, config, files):
    def repl(match):
        target = match.group(1).strip()
        text = match.group(2)

        if ":" in target:
            prefix, rest = target.split(":", 1)
            label = text or rest

            if prefix in interwiki:
                url = interwiki[prefix].format(page=rest.replace(" ", "_"))
                title = f"Visit '{rest}' on external Wiki"

                if "//" in url:
                    domain = url.split("//", 1)[1]
                    if "/" in domain:
                        domain = domain.split("/", 1)[0]
                    
                    title = f"Visit '{rest}' on {domain}"
                
                return f'<span title="{title}"><a href="{url}" target="_blank" rel="noopener">{label}</a></span>'
            else:
                log.warning(f'Unknown Interwiki prefix "{prefix}" in "{page.file.src_uri}"!')
                return f'<span class="red-link" title="Unknown Interiki prefix \'{prefix}\'">{label}</span>'
        
        label = text or target

        alias = check_aliases(target)
        if alias:
            target = alias
        
        resolved = resolve_internal(target, page.file)

        if not resolved:
            log.warning(f'Unknown target page "{target}" in "{page.file.src_uri}"!')

            return f'<span class="red-link" title="Unknown Wiki page \'{target}\'">{label}</span>'
        
        link = make_relative(page.file, resolved)
        return f'[{label}]({link})'
    
    if page.meta and page.meta.get("no_wikilinks", False):
        log.info(f'"no_wikilinks" meta found in page "{page.file.src_uri}". Skipping...')
        return markdown
    
    return WIKILINK_PATTERN.sub(repl, markdown)