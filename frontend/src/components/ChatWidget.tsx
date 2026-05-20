import { useEffect, useRef, useState, FormEvent } from 'react';
import { MessageCircle, X, Send, Sparkles, Bot } from 'lucide-react';
import { chatApi } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';
import type { ChatSuggestion } from '../types';

interface Message {
  id: string;
  from: 'user' | 'bot';
  text: string;
  suggestions?: ChatSuggestion[] | null;
}

const uid = () => Math.random().toString(36).slice(2);

export default function ChatWidget() {
  const { user } = useAuth();
  const [open, setOpen] = useState(false);
  const [input, setInput] = useState('');
  const [busy, setBusy] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const scrollerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (open && messages.length === 0 && user) {
      setMessages([{
        id: uid(),
        from: 'bot',
        text: `Hi ${user.firstName}! I'm your library assistant. Ask me about your borrows, reservations, or how to find a book.`,
        suggestions: [
          { label: 'My borrows', value: 'What did I borrow?' },
          { label: 'My reservations', value: 'What did I reserve?' },
          { label: 'Find a book', value: 'How do I search the catalog?' },
          { label: 'Late fees', value: 'How are late fees calculated?' },
        ],
      }]);
    }
  }, [open, messages.length, user]);

  useEffect(() => {
    scrollerRef.current?.scrollTo({ top: scrollerRef.current.scrollHeight, behavior: 'smooth' });
  }, [messages, busy]);

  if (!user) return null;

  const send = async (text: string) => {
    const trimmed = text.trim();
    if (!trimmed || busy) return;

    const userMsg: Message = { id: uid(), from: 'user', text: trimmed };
    setMessages((m) => [...m, userMsg]);
    setInput('');
    setBusy(true);

    try {
      const reply = await chatApi.send(trimmed);
      setMessages((m) => [...m, {
        id: uid(),
        from: 'bot',
        text: reply.reply,
        suggestions: reply.suggestions,
      }]);
    } catch {
      setMessages((m) => [...m, {
        id: uid(),
        from: 'bot',
        text: 'Sorry, I had trouble reaching the server. Please try again.',
      }]);
    } finally {
      setBusy(false);
    }
  };

  const onSubmit = (e: FormEvent) => {
    e.preventDefault();
    send(input);
  };

  return (
    <>
      <button
        onClick={() => setOpen((o) => !o)}
        aria-label={open ? 'Close assistant' : 'Open assistant'}
        className="fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full bg-gradient-to-br from-brand-500 to-brand-700
                   text-white shadow-xl hover:shadow-2xl hover:scale-105 active:scale-95
                   transition grid place-items-center"
      >
        {open ? <X size={22} /> : <MessageCircle size={24} />}
      </button>

      {open && (
        <div
          role="dialog"
          aria-label="Library assistant"
          className="fixed bottom-24 right-6 z-50 w-[min(380px,calc(100vw-3rem))] h-[min(560px,calc(100vh-8rem))]
                     card flex flex-col overflow-hidden shadow-2xl animate-in fade-in slide-in-from-bottom-4"
        >
          <header className="px-4 py-3 bg-gradient-to-br from-brand-600 to-brand-800 text-white">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-lg bg-white/15 grid place-items-center">
                <Sparkles size={18} />
              </div>
              <div className="flex-1">
                <h2 className="font-semibold leading-tight">Library Assistant</h2>
                <p className="text-xs text-brand-100/90">Ask anything about your library</p>
              </div>
            </div>
          </header>

          <div ref={scrollerRef} className="flex-1 p-4 overflow-y-auto scroll-pretty space-y-3 bg-slate-50 dark:bg-slate-950">
            {messages.map((m) => (
              <div key={m.id}>
                <div className={`flex gap-2 ${m.from === 'user' ? 'justify-end' : 'justify-start'}`}>
                  {m.from === 'bot' && (
                    <div className="w-7 h-7 shrink-0 rounded-full bg-brand-100 dark:bg-brand-900 text-brand-700 dark:text-brand-300 grid place-items-center">
                      <Bot size={14} />
                    </div>
                  )}
                  <div className={`max-w-[78%] rounded-2xl px-3 py-2 text-sm whitespace-pre-wrap leading-relaxed ${
                    m.from === 'user'
                      ? 'bg-brand-600 text-white rounded-br-sm'
                      : 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-bl-sm'
                  }`}>
                    {m.text}
                  </div>
                </div>
                {m.suggestions && m.suggestions.length > 0 && (
                  <div className="flex flex-wrap gap-2 mt-2 ml-9">
                    {m.suggestions.map((s, i) => (
                      <button
                        key={i}
                        onClick={() => send(s.value)}
                        className="text-xs px-3 py-1 rounded-full bg-white dark:bg-slate-900 border
                                   border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-200
                                   hover:bg-brand-50 dark:hover:bg-brand-900/30 hover:border-brand-300 transition"
                      >
                        {s.label}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {busy && (
              <div className="flex gap-2 justify-start">
                <div className="w-7 h-7 shrink-0 rounded-full bg-brand-100 dark:bg-brand-900 text-brand-700 dark:text-brand-300 grid place-items-center">
                  <Bot size={14} />
                </div>
                <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl rounded-bl-sm px-3 py-2">
                  <span className="inline-flex gap-1">
                    <span className="w-1.5 h-1.5 bg-slate-400 rounded-full animate-bounce [animation-delay:-0.3s]" />
                    <span className="w-1.5 h-1.5 bg-slate-400 rounded-full animate-bounce [animation-delay:-0.15s]" />
                    <span className="w-1.5 h-1.5 bg-slate-400 rounded-full animate-bounce" />
                  </span>
                </div>
              </div>
            )}
          </div>

          <form onSubmit={onSubmit} className="border-t border-slate-200 dark:border-slate-800 p-3 bg-white dark:bg-slate-900 flex gap-2">
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask the assistant…"
              className="input"
              disabled={busy}
              autoFocus
            />
            <button type="submit" disabled={busy || !input.trim()} className="btn-primary px-3">
              <Send size={16} />
            </button>
          </form>
        </div>
      )}
    </>
  );
}
