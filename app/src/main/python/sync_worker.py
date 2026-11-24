import asyncio
import json
import os
import random
import threading
from telethon import TelegramClient, events
from telethon.errors import FloodWaitError, SessionPasswordNeededError

_loop = asyncio.new_event_loop()
_client = None
_is_running = False
_status_text = "Idle"
_copied_count = 0

def _start_bg_loop():
    asyncio.set_event_loop(_loop)
    _loop.run_forever()

threading.Thread(target=_start_bg_loop, daemon=True).start()

def _run(coro):
    return asyncio.run_coroutine_threadsafe(coro, _loop).result(timeout=30)

def init_client(files_dir, api_id, api_hash):
    global _client
    session_path = os.path.join(files_dir, "telesync_session")
    async def _init():
        global _client
        if _client is not None:
            try:
                await _client.disconnect()
            except Exception:
                pass
        _client = TelegramClient(
            session_path,
            int(api_id),
            api_hash.strip(),
            device_model="Samsung Galaxy S23",
            system_version="Android 14",
            app_version="10.14.5",
            lang_code="en",
            system_lang_code="en"
        )
        await _client.connect()
        return await _client.is_user_authorized()
    return _run(_init())

def send_code(phone):
    async def _send():
        res = await _client.send_code_request(phone.strip())
        return res.phone_code_hash
    return _run(_send())

def sign_in(phone, code, phone_hash, password=""):
    async def _login():
        try:
            await _client.sign_in(phone=phone.strip(), code=code.strip(), phone_code_hash=phone_hash)
            return "OK"
        except SessionPasswordNeededError:
            if not password:
                return "2FA_REQUIRED"
            await _client.sign_in(password=password)
            return "OK"
        except Exception as e:
            return f"ERR: {e}"
    return _run(_login())

def get_channels():
    async def _fetch():
        channels = []
        async for dialog in _client.iter_dialogs():
            if dialog.is_channel:
                channels.append({"id": dialog.id, "title": dialog.title})
        return json.dumps(channels)
    return _run(_fetch())

def get_status():
    return json.dumps({
        "running": _is_running,
        "status": _status_text,
        "count": _copied_count
    })

def stop_sync():
    global _is_running, _status_text
    _is_running = False
    _status_text = "Stopped"

def start_sync(files_dir, source_id, target_id, hide_forward, newest_first, max_per_hour):
    global _is_running, _status_text
    if _is_running:
        return
    _is_running = True
    asyncio.run_coroutine_threadsafe(
        _sync_task(files_dir, int(source_id), int(target_id), hide_forward, newest_first, int(max_per_hour)),
        _loop
    )

async def _copy_single(msg, target_id, hide_forward):
    while _is_running:
        try:
            if hide_forward:
                await _client.send_message(target_id, msg)
            else:
                await _client.forward_messages(target_id, msg)
            return True
        except FloodWaitError as e:
            global _status_text
            _status_text = f"Telegram pause ({e.seconds}s)..."
            await asyncio.sleep(e.seconds + 5)
        except Exception:
            return False
    return False

async def _sync_task(files_dir, source_id, target_id, hide_forward, newest_first, max_per_hour):
    global _is_running, _status_text, _copied_count
    state_file = os.path.join(files_dir, f"state_{source_id}_{target_id}.json")
    done_ids = set()
    if os.path.exists(state_file):
        with open(state_file, "r") as f:
            done_ids = set(json.load(f))

    @_client.on(events.NewMessage(chats=source_id))
    async def _on_new(event):
        global _copied_count
        if not _is_running or event.message.id in done_ids:
            return
        if not event.message.text and not event.message.media:
            return
        await asyncio.sleep(random.uniform(3, 8))
        if await _copy_single(event.message, target_id, hide_forward):
            done_ids.add(event.message.id)
            _copied_count += 1
            with open(state_file, "w") as f:
                json.dump(list(done_ids), f)

    _status_text = "Indexing channel history..."
    base_delay = 3600.0 / max(1, max_per_hour)

    async for msg in _client.iter_messages(source_id, reverse=(not newest_first)):
        if not _is_running:
            break
        if msg.id in done_ids or (not msg.text and not msg.media):
            continue

        _status_text = f"Copying post #{msg.id}..."
        if await _copy_single(msg, target_id, hide_forward):
            done_ids.add(msg.id)
            _copied_count += 1
            with open(state_file, "w") as f:
                json.dump(list(done_ids), f)

            wait_sec = random.uniform(base_delay * 0.88, base_delay * 1.15)
            _status_text = f"Waiting {int(wait_sec)}s (~{max_per_hour}/hr limit)..."
            for _ in range(int(wait_sec)):
                if not _is_running:
                    break
                await asyncio.sleep(1)

    if _is_running:
        _status_text = "History complete. Watching for new posts..."
